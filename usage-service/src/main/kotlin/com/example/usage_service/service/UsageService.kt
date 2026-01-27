package com.example.usage_service.service

import com.example.kafka.event.AlertingEvent
import com.example.kafka.event.EnergyUsageEvent
import com.example.usage_service.client.DeviceClient
import com.example.usage_service.client.UserClient
import com.example.usage_service.dto.UserDto
import com.example.usage_service.model.DeviceEnergy
import com.example.usage_service.utils.logger
import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class UsageService(
    private val influxDBClient: InfluxDBClient,
    private val deviceClient: DeviceClient,
    private val userClient: UserClient,
    private val kafkaTemplate: KafkaTemplate<String, AlertingEvent>,
    @Value("\${influx.bucket}")
    private val influxBucket: String,
    @Value("\${influx.org}")
    private val influxOrg: String,
) {

    companion object {
        val log = logger()
    }

    @KafkaListener(topics = ["energy-usage"], groupId = "usage-service")
    fun onEnergyUsageEvent(event: EnergyUsageEvent) {
        val point = Point
            .measurement("energy_usage")
            .addTag("deviceId", event.deviceId.toString())
            .addField("energyConsumed", event.energyConsumed)
            .time(event.timestamp, WritePrecision.MS)

        influxDBClient
            .writeApiBlocking
            .writePoint(influxBucket, influxOrg, point)
    }

    @Scheduled(cron = "*/10 * * * * *")
    fun aggregateDeviceEnergyUsage() {
        val now = Instant.now()
        val oneHourAgo = now.minus(Duration.ofHours(1))

        val fluxQuery = """
            from(bucket: "$influxBucket")
              |> range(start: time(v: "$oneHourAgo"), stop: time(v: "$now"))
              |> filter(fn: (r) => r["_measurement"] == "energy_usage")
              |> filter(fn: (r) => r["_field"] == "energyConsumed")
              |> group(columns: ["deviceId"])
              |> sum(column: "_value")
        """.trimIndent()

        val tables = influxDBClient
            .queryApi
            .query(fluxQuery, influxOrg)

        val deviceEnergies = tables
            .flatMap { it.records }
            .mapNotNull { record ->
                val deviceId = record.getValueByKey("deviceId") as? String ?: return@mapNotNull null
                val energy = (record.getValueByKey("_value") as? Number)?.toDouble() ?: 0.0

                DeviceEnergy(
                    deviceId = deviceId.toLong(),
                    energyConsumed = energy
                )
            }

        log.info("Aggregated device energies: {}", deviceEnergies)

        deviceEnergies.forEach { deviceEnergy ->
            runCatching {
                deviceClient.getDeviceById(deviceEnergy.deviceId)
            }.onSuccess { device ->
                deviceEnergy.userId = device?.userId
            }.onFailure {
                log.warn("Failed to fetch device {}", deviceEnergy.deviceId)
            }
        }

        val validDevices = deviceEnergies.filter { it.userId != null }
        val userDeviceEnergyMap = validDevices.groupBy { it.userId!! }
        val userThresholdMap = mutableMapOf<Long, Double>()
        val userEmailMap = mutableMapOf<Long, String>()

        userDeviceEnergyMap.keys.forEach { userId ->
            runCatching {
                userClient.getUserById(userId)
            }.onSuccess { user ->
                if (user != null && user.alerting) {
                    userThresholdMap[userId] = user.energyAlertingThreshold
                    userEmailMap[userId] = user.email
                }
            }.onFailure {
                log.warn("Failed to fetch user {}", userId)
            }
        }

        userThresholdMap.forEach { (userId, threshold) ->
            val totalConsumption = userDeviceEnergyMap[userId]
                ?.sumOf { it.energyConsumed }
                ?: 0.0

            if (totalConsumption > threshold) {
                val alert = AlertingEvent(
                    userId = userId,
                    message = "Energy consumption threshold exceeded",
                    threshold = threshold,
                    energyConsumed = totalConsumption,
                    email = userEmailMap[userId]
                )

                kafkaTemplate.send("energy-alerts", alert)

                log.info(
                    "ALERT userId={}, total={}, threshold={}",
                    userId, totalConsumption, threshold
                )
            }
        }
    }
}