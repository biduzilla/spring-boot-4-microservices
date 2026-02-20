package com.example.insight_service.client

import com.example.insight_service.dto.UsageDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class UsageClient(
    @Value($$"${usage.service.url}")
    private val baseUrl: String,
    private val restTemplate: RestTemplate = RestTemplate()
) {
    fun getXDaysUsageForUser(userId: Long, days:Int): UsageDto? {
        val url = UriComponentsBuilder
            .fromUriString(baseUrl)
            .pathSegment(userId.toString())
            .queryParam("days",days)
            .toUriString()

        return restTemplate.getForObject(url, UsageDto::class.java)
    }
}