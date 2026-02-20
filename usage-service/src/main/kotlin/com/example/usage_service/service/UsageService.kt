package com.example.usage_service.service

import com.example.kafka.event.AlertingEvent
import com.example.kafka.event.EnergyUsageEvent
import com.example.usage_service.client.DeviceClient
import com.example.usage_service.client.UserClient
import com.example.usage_service.dto.UsageDto
import com.example.usage_service.dto.UserDto
import com.example.usage_service.dto.toModel
import com.example.usage_service.model.DeviceEnergy
import com.example.usage_service.model.toDto
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

    fun getXDaysUsageForUser(userId: Long, days: Int): UsageDto {
        log.info("Getting usage for userId $userId over past $days")
        val devicesDto = deviceClient.getAllDevicesForUser(userId)

        val devices = devicesDto.map { it.toModel() }.toMutableList()

        if (devices.isEmpty()) {
            return UsageDto(
                userId,
                emptyList()
            )
        }

        val deviceIdStrings = devices
            .map { it.id }
            .map { it.toString() }

        val now = Instant.now()
        val start = now.minusSeconds(days.toLong() * 24 * 3600)

        val deviceFilter = deviceIdStrings
            .joinToString(" or ") { """r["deviceId"] == "$it"""" }

        val fluxQuery = """
        from(bucket: "$influxBucket")
          |> range(start: time(v: "$start"), stop: time(v: "$now"))
          |> filter(fn: (r) => r["_measurement"] == "energy_usage")
          |> filter(fn: (r) => r["_field"] == "energyConsumed")
          |> filter(fn: (r) => $deviceFilter)
          |> group(columns: ["deviceId"])
          |> sum(column: "_value")
    """.trimIndent()

        val aggregatedMap = mutableMapOf<Long, Double>()

        try {
            val queryApi = influxDBClient.queryApi
            val tables = queryApi.query(fluxQuery, influxOrg)

            for (table in tables) {
                for (record in table.records) {
                    val deviceIdStr = record.getValueByKey("deviceId")
                        ?.toString() ?: continue

                    val energyConsumed =
                        (record.getValueByKey("_value") as? Number)?.toDouble() ?: 0.0

                    try {
                        val deviceId = deviceIdStr.toLong()
                        aggregatedMap[deviceId] =
                            aggregatedMap.getOrDefault(deviceId, 0.0) + energyConsumed
                    } catch (e: NumberFormatException) {
                        log.warn("Failed to parse deviceId from flux record: {}", deviceIdStr)
                    }
                }
            }
        } catch (e: Exception) {
            log.error(
                "Failed to query InfluxDB for user {} usage over {} days: {}",
                userId,
                days,
                e.message
            )
            devices.forEach { it.energyConsumed = 0.0 }

            return UsageDto(
                userId,
                emptyList()
            )
        }

        devices.forEach {
            it.energyConsumed = aggregatedMap.getOrDefault(it.id, 0.0)
        }

        log.info("Aggregated energy consumption for userId {}: {}", userId, aggregatedMap)

        val resultDevices = devices.map { it.toDto() }

        return UsageDto(
            userId,
            resultDevices
        )
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

//    @Scheduled(cron = "*/10 * * * * *")
//    fun aggregateDeviceEnergyUsage() {
//        val now = Instant.now()
//        val oneHourAgo = now.minus(Duration.ofHours(1))
//
//        val fluxQuery = """
//            from(bucket: "$influxBucket")
//              |> range(start: time(v: "$oneHourAgo"), stop: time(v: "$now"))
//              |> filter(fn: (r) => r["_measurement"] == "energy_usage")
//              |> filter(fn: (r) => r["_field"] == "energyConsumed")
//              |> group(columns: ["deviceId"])
//              |> sum(column: "_value")
//        """.trimIndent()
//
//        val tables = influxDBClient
//            .queryApi
//            .query(fluxQuery, influxOrg)
//
//        val deviceEnergies = tables
//            .flatMap { it.records }
//            .mapNotNull { record ->
//                val deviceId = record.getValueByKey("deviceId") as? String ?: return@mapNotNull null
//                val energy = (record.getValueByKey("_value") as? Number)?.toDouble() ?: 0.0
//
//                DeviceEnergy(
//                    deviceId = deviceId.toLong(),
//                    energyConsumed = energy
//                )
//            }
//
//        log.info("Aggregated device energies: {}", deviceEnergies)
//
//        deviceEnergies.forEach { deviceEnergy ->
//            runCatching {
//                deviceClient.getDeviceById(deviceEnergy.deviceId)
//            }.onSuccess { device ->
//                deviceEnergy.userId = device?.userId
//            }.onFailure {
//                log.warn("Failed to fetch device {}", deviceEnergy.deviceId)
//            }
//        }
//
//        val validDevices = deviceEnergies.filter { it.userId != null }
//        val userDeviceEnergyMap = validDevices.groupBy { it.userId!! }
//        val userThresholdMap = mutableMapOf<Long, Double>()
//        val userEmailMap = mutableMapOf<Long, String>()
//
//        userDeviceEnergyMap.keys.forEach { userId ->
//            runCatching {
//                userClient.getUserById(userId)
//            }.onSuccess { user ->
//                if (user != null && user.alerting) {
//                    userThresholdMap[userId] = user.energyAlertingThreshold
//                    userEmailMap[userId] = user.email
//                }
//            }.onFailure {
//                log.warn("Failed to fetch user {}", userId)
//            }
//        }
//
//        userThresholdMap.forEach { (userId, threshold) ->
//            val totalConsumption = userDeviceEnergyMap[userId]
//                ?.sumOf { it.energyConsumed }
//                ?: 0.0
//
//            if (totalConsumption > threshold) {
//                val alert = AlertingEvent(
//                    userId = userId,
//                    message = "Energy consumption threshold exceeded",
//                    threshold = threshold,
//                    energyConsumed = totalConsumption,
//                    email = userEmailMap[userId]
//                )
//
//                kafkaTemplate.send("energy-alerts", alert)
//
//                log.info(
//                    "ALERT userId={}, total={}, threshold={}",
//                    userId, totalConsumption, threshold
//                )
//            }
//        }
//    }

    @Scheduled(cron = "*/10 * * * * *")
    fun aggregateDeviceEnergyUsage() {
        val deviceEnergies = fetchDeviceEnergiesLastHour()
        log.info("Aggregated device energies: {}", deviceEnergies)
        if (deviceEnergies.isEmpty()) return

        val deviceUserMap = resolveDevicesUsers(deviceEnergies)
        val userConsumptions = aggregateByUser(deviceEnergies, deviceUserMap)
        val users = fetchUsers(userConsumptions.keys)

        sendAlertsIfNeeded(userConsumptions, users)
    }


    private fun fetchDeviceEnergiesLastHour(): List<DeviceEnergy> {
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

        return influxDBClient.queryApi
            .query(fluxQuery, influxOrg)
            .flatMap { it.records }
            .mapNotNull { record ->
                val deviceId = record.getValueByKey("deviceId") as? String ?: return@mapNotNull null
                val energy = (record.getValueByKey("_value") as? Number)?.toDouble() ?: return@mapNotNull null

                DeviceEnergy(deviceId.toLong(), energy)
            }
    }

    private fun resolveDevicesUsers(
        deviceEnergies: List<DeviceEnergy>
    ): Map<Long, Long> {
        return deviceEnergies
            .map { it.deviceId }
            .distinct()
            .associateWith { deviceId ->
                runCatching {
                    deviceClient.getDeviceById(deviceId)?.userId
                }.getOrElse {
                    log.warn("Failed to fetch device {}", deviceId, it)
                    null
                }
            }.filterValues { it != null }
            .mapValues { it.value!! }
    }

    private fun aggregateByUser(
        deviceEnergies: List<DeviceEnergy>,
        deviceUserMap: Map<Long, Long>
    ): Map<Long, Double> {
        return deviceEnergies
            .mapNotNull { energy ->
                deviceUserMap[energy.deviceId]?.let { userId ->
                    userId to energy.energyConsumed
                }
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.sum() }
    }

    private fun fetchUsers(userIds: Set<Long>): Map<Long, UserDto> {
        return userIds.associateWith { userId ->
            runCatching {
                userClient.getUserById(userId)
            }.getOrElse {
                log.warn("Failed to fetch user {}", userId, it)
                null
            }
        }.filterValues { it != null }
            .mapValues { it.value!! }
    }

    private fun sendAlertsIfNeeded(
        userConsumptions: Map<Long, Double>,
        users: Map<Long, UserDto>
    ) {
        userConsumptions.forEach { (userId, totalConsumption) ->
            val user = users[userId] ?: return@forEach
            if (!user.alerting) return@forEach

            if (totalConsumption > user.energyAlertingThreshold) {
                val alert = AlertingEvent(
                    userId = userId,
                    message = "Energy consumption threshold exceeded",
                    threshold = user.energyAlertingThreshold,
                    energyConsumed = totalConsumption,
                    email = user.email
                )

                kafkaTemplate.send("energy-alerts", alert)

                log.info(
                    "ALERT userId={}, total={}, threshold={}",
                    userId, totalConsumption, user.energyAlertingThreshold
                )
            }
        }
    }

}