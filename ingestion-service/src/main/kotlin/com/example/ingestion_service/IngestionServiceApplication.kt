package com.example.ingestion_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class IngestionServiceApplication

fun main(args: Array<String>) {
	runApplication<IngestionServiceApplication>(*args)
}
