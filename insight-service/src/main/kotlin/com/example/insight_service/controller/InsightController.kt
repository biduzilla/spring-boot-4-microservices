package com.example.insight_service.controller

import com.example.insight_service.dto.InsightDto
import com.example.insight_service.service.InsightService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/insight")
class InsightController(
    private val insightService: InsightService
) {
    @GetMapping("/saving-tips/{userId")
    fun getSavingTips(@PathVariable userId: Long): ResponseEntity<InsightDto> {
        val insight = insightService.getSavingsTips(userId)
        return ResponseEntity.ok(insight)
    }

    @GetMapping("/overview/{userId")
    fun getOverview(@PathVariable userId: Long): ResponseEntity<InsightDto> {
        val insight = insightService.getOverview(userId)
        return ResponseEntity.ok(insight)
    }

}