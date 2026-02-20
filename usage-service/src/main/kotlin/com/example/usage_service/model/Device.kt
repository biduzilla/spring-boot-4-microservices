package com.example.usage_service.model

import com.example.usage_service.dto.DeviceDto

data class Device(
    val id: Long,
    val name: String,
    val type: String,
    val location: String,
    val userId: Long,
    var energyConsumed: Double
)

fun Device.toDto(): DeviceDto {
    return DeviceDto(
        id,
        name,
        type,
        location,
        userId,
        energyConsumed
    )
}
