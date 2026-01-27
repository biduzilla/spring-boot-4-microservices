package com.example.alert_service.service

import com.example.alert_service.utils.logger
import com.example.kafka.event.AlertingEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class AlertService(
    private val emailService: EmailService
) {
    companion object {
        val log = logger()
    }

    @KafkaListener(topics = ["energy-alerts"], groupId = "alert-service")
    fun energyUsageAlertEvent(alertingEvent: AlertingEvent) {
        log.info("Received alert event: {}", alertingEvent)

        val subject = "Energy Usage Alert for User ${alertingEvent.userId}"
        val message = """
            Alert: ${alertingEvent.message}
            Threshold: ${alertingEvent.threshold}
            Energy Consumed: ${alertingEvent.energyConsumed} 
        """.trimIndent()

        emailService.sendEmail(
            alertingEvent.email ?: "",
            subject,
            message,
            alertingEvent.userId
        )
    }
}