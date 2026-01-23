package com.example.usage_service.service

import com.example.kafka.event.EnergyUsageEvent
import com.example.usage_service.utils.logger
import com.influxdb.client.InfluxDBClient
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class UsageService(
    private val influxDBClient: InfluxDBClient,
    @Value($$"${influx.bucket}")
    private val influxBucket: String,
    @Value($$"${influx.org}")
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
}