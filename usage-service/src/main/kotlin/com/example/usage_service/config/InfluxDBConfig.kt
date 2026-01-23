package com.example.usage_service.config

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InfluxDBConfig(
    @Value($$"${influx.url}")
    private val influxUrl: String,

    @Value($$"${influx.token}")
    private val influxToken: String,

    @Value($$"${influx.org}")
    private val influxOrg: String,
) {


    @Bean
    fun influxDBClient(): InfluxDBClient {
        return InfluxDBClientFactory.create(
            influxUrl,
            influxToken.toCharArray(),
            influxOrg,
        )
    }
}