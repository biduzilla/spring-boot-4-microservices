package com.example.insight_service.service

import com.example.insight_service.client.UsageClient
import com.example.insight_service.dto.InsightDto
import com.example.insight_service.utils.logger
import org.springframework.stereotype.Service

@Service
class InsightService(
    private val usageClient: UsageClient
) {
    companion object {
        val log = logger()
    }

    fun getOverview(userId: Long): InsightDto {
        val usageData = usageClient.getXDaysUsageForUser(userId, 3)
    }
}