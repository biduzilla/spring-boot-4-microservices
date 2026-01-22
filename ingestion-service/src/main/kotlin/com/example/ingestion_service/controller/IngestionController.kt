package com.example.ingestion_service.controller

import com.example.ingestion_service.dto.EnergyUsageDto
import com.example.ingestion_service.service.IngestionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ingestion")
class IngestionController(
    private val ingestionService: IngestionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun ingestData(@RequestBody usageDto: EnergyUsageDto){
        ingestionService.ingestEnergyUsage(usageDto)
    }
}