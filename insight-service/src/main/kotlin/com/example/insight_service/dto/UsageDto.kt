package com.example.insight_service.dto

data class UsageDto(
    var userId:Long = 0L,
    var devices:List<DeviceDto> = emptyList()
)
