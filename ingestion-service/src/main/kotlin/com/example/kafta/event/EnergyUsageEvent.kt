package com.example.kafta.event

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

data class EnergyUsageEvent(
    val deviceId: Long,
    val energyConsumed: Double,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val timestamp: Instant
)
