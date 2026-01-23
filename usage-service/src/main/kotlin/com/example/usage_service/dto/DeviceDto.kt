package com.example.usage_service.dto

data class DeviceDto(
    val id: Long,
    val name: String,
    val type: String,
    val location: String,
    val userId: Long
)
