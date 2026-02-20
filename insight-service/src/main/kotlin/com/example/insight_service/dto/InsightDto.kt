package com.example.insight_service.dto

data class InsightDto(
    var userId: Long = 0L,
    var tips: String = "",
    var energyUsage: Double = 0.0
)
