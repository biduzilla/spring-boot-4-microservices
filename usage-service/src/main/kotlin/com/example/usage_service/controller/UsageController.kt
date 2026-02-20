package com.example.usage_service.controller

import com.example.usage_service.dto.UsageDto
import com.example.usage_service.service.UsageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/usage")
class UsageController(
    private val usageService: UsageService
) {

    @GetMapping("/{userId}")
    fun getUserDviceUsage(
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "3") days: Int
    ): ResponseEntity<UsageDto> {
        val usage = usageService.getXDaysUsageForUser(userId, days)
        return ResponseEntity.ok(usage)
    }
}