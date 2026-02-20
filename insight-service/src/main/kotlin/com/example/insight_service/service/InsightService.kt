package com.example.insight_service.service

import com.example.insight_service.client.UsageClient
import com.example.insight_service.dto.InsightDto
import com.example.insight_service.utils.logger
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.stereotype.Service

@Service
class InsightService(
    private val usageClient: UsageClient,
    private val ollamaChatModel: OllamaChatModel
) {
    companion object {
        val log = logger()
    }

    fun getSavingsTips(userId: Long): InsightDto {
        val usageData = usageClient.getXDaysUsageForUser(userId, 3)
        val totalUsage = usageData?.devices
            ?.sumOf { it.energyConsumed }
            ?: 0.0

        log.info(
            "Calling Ollama for userId {} with total usage {}",
            userId,
            totalUsage
        )

        val prompt = """
        This is my total consumption over the past 3 days.
        How can I reduce my energy consumption?
        How does it compare to average households?
        Total energy used:
        $totalUsage
    """.trimIndent()

        val response = ollamaChatModel.call(
            Prompt.builder()
                .content(prompt)
                .build()
        )

        return InsightDto(
            userId,
            tips = response.result?.output?.text.toString(),
            energyUsage = totalUsage
        )
    }

    fun getOverview(userId: Long): InsightDto {
        val usageData = usageClient.getXDaysUsageForUser(userId, 3)

        val totalUsage = usageData?.devices
            ?.sumOf { it.energyConsumed }
            ?: 0.0

        log.info(
            "Calling Ollama for userId {} with total usage {}",
            userId,
            totalUsage
        )

        val prompt = """
        Analyse the following energy usage data and provide a concise overview 
        with actionable insights.
        This data is the aggregate data for the past 3 days.
        
        Usage Data:
        ${usageData?.devices}
    """.trimIndent()

        val response = ollamaChatModel.call(
            Prompt.builder()
                .content(prompt)
                .build()
        )

        return InsightDto(
            userId,
            tips = response.result?.output?.text.toString(),
            energyUsage = totalUsage
        )
    }
}