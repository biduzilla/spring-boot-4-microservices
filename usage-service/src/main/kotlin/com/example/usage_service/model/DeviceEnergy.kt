package com.example.usage_service.model

data class DeviceEnergy(
    var deviceId: Long = 0L,
    var energyConsumed: Double = 0.0,
    var userId: Long? = null
)
