package com.example.ingestion_service.simulation

import com.example.ingestion_service.dto.EnergyUsageDto
import com.example.ingestion_service.utils.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.round
import kotlin.random.Random

@Component
class ContinuousDataSimulator(
    @Value($$"${simulation.requests-per-interval}")
    private val requestsPerInterval: Int,

    @Value($$"${simulation.endpoint}")
    private val ingestionEndpoint: String
) : CommandLineRunner {

    private val restTemplate = RestTemplate()
    private val random = Random.Default

    companion object {
        val log = logger()
    }

    override fun run(vararg args: String) {
        log.info("ContinuousDataSimulator started")
    }

//    @Scheduled(fixedRateString = $$"${simulation.interval-ms}")
    fun sendMockData() {
        repeat(requestsPerInterval) {
            val dto = EnergyUsageDto(
                deviceId = random.nextLong(1, 6),
                energyConsumed = round(
                    random.nextDouble(0.0, 2.0) * 100.0
                ) / 100.0,
                timestamp = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            )

            try {
                val headers = HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }

                val request = HttpEntity(dto, headers)
                restTemplate.postForEntity(ingestionEndpoint, request, Void::class.java)

                log.info("Sent mock data: {}", dto)
            } catch (e: Exception) {
                log.error("Failed to send data", e)
            }
        }
    }

}