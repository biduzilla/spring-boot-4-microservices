package com.example.alert_service.service

import com.example.alert_service.entity.Alert
import com.example.alert_service.repository.AlertRepository
import com.example.alert_service.utils.logger
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    val alertRepository: AlertRepository
) {

    companion object {
        val log = logger()
    }

    fun sendEmail(
        to: String,
        subject: String,
        body: String,
        userId: Long
    ) {
        log.info("Sending email to: {}, subject: {}", to, subject)

        val message = SimpleMailMessage().apply {
            setTo(to)
            from = "noreply@leetjourney.com"
            setSubject(subject)
            text = body
        }

        try {
            mailSender.send(message)

            val alertSent = Alert(
                sent = true,
                createdAt = LocalDateTime.now(),
                userID = userId,
            )

            alertRepository.saveAndFlush(alertSent)
            log.info("Email sent to: {}", to)
        } catch (e: MailException) {
            log.error("Failed to send email to: {}", to, e)

            val alertSent = Alert(
                sent = false,
                createdAt = LocalDateTime.now(),
                userID = userId,
            )

            alertRepository.saveAndFlush(alertSent)
        }
    }
}