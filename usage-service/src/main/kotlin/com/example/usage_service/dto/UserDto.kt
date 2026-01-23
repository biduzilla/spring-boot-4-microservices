package com.example.usage_service.dto

data class UserDto(
    val id: Long,
    val name: String,
    val surname: String,
    val email: String,
    val address: String,
    val alerting: Boolean,
    val energyAlertingThreshold: Double,
)
