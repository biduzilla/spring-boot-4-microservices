package com.example.ingestion_service.dto

import com.example.kafka.event.EnergyUsageEvent
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant


data class EnergyUsageDto(
    val deviceId: Long,
    val energyConsumed: Double,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val timestamp: Instant
)

fun EnergyUsageDto.toEvent(): EnergyUsageEvent {
    return EnergyUsageEvent(
        deviceId = deviceId,
        energyConsumed = energyConsumed,
        timestamp = timestamp
    )
}
