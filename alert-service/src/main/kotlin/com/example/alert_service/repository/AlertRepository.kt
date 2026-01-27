package com.example.alert_service.repository

import com.example.alert_service.entity.Alert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AlertRepository : JpaRepository<Alert, Long> {
}