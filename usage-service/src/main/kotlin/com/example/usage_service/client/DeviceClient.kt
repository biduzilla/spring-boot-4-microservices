package com.example.usage_service.client

import com.example.usage_service.dto.DeviceDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class DeviceClient(
    @Value("\${device.service.url}")
    private val baseUrl: String,
    private val restTemplate: RestTemplate = RestTemplate()
) {
    fun getDeviceById(deviceId: Long): DeviceDto? {
        val url = UriComponentsBuilder
            .fromUriString(baseUrl)
            .pathSegment(deviceId.toString())
            .toUriString()

        return restTemplate.getForObject(url, DeviceDto::class.java)
    }

    fun getAllDevicesForUser(userId: Long): List<DeviceDto> {
        var url = UriComponentsBuilder
            .fromUriString(baseUrl)
            .path("/user/{userId")
            .buildAndExpand(userId)
            .toUriString()

        val response = restTemplate.getForEntity(url, Array<DeviceDto>::class.java)
        val devices = response.body

        return devices?.toList() ?: emptyList()
    }
}