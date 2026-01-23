package com.example.usage_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class UsageServiceApplication

fun main(args: Array<String>) {
	runApplication<UsageServiceApplication>(*args)
}
