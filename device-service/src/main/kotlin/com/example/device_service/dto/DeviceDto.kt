package com.example.device_service.dto

import com.example.device_service.entity.Device
import com.example.device_service.model.DeviceType

data class DeviceDto(
    var id: Long? = null,
    var name: String,
    var type: DeviceType,
    var location: String,
    var userId: Long,
)

fun DeviceDto.toDevice(): Device {
    return Device(
        this.id,
        this.name,
        this.type,
        this.location,
        this.userId,
    )
}
