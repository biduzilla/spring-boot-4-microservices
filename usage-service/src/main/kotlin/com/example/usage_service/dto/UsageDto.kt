package com.example.usage_service.dto

data class UsageDto(
    val userId: Long,
    val devices: List<DeviceDto>
)
