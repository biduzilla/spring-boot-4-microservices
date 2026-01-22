package com.example.ingestion_service.service

import com.example.ingestion_service.dto.EnergyUsageDto
import com.example.ingestion_service.dto.toEvent
import com.example.ingestion_service.utils.logger
import com.example.kafta.event.EnergyUsageEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class IngestionService(
    private val kafkaTemplate: KafkaTemplate<String, EnergyUsageEvent>
) {
    companion object {
        val log = logger()
    }

    fun ingestEnergyUsage(input: EnergyUsageDto) {
        val event = input.toEvent()
        kafkaTemplate.send("energy-usage", event)
        log.info("Ingested Energy Usage Event: {}", event)
    }
}