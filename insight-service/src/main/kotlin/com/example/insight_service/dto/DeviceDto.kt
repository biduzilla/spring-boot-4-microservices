package com.example.insight_service.dto

data class DeviceDto(
    var id: Long = 0L,
    var name: String = "",
    var type: String = "",
    var location: String = "",
    var energyConsumed: Double = 0.0
)
