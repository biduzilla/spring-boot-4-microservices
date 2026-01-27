package com.example.alert_service.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "alert")
data class Alert(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    var userId: Long,
    var createdAt: LocalDateTime,
    var sent: Boolean
)
