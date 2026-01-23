package com.example.usage_service.client

import com.example.usage_service.dto.UserDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class UserClient(
    @Value($$"${user.service.url}")
    private val baseUrl: String,
    private val restTemplate: RestTemplate = RestTemplate()
) {

    fun getUserById(userId: Long): UserDto? {
        val url = UriComponentsBuilder
            .fromUriString(baseUrl)
            .pathSegment(userId.toString())
            .toUriString()

        return restTemplate.getForObject(url, UserDto::class.java)
    }
}