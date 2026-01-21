package com.example.device_service.repository

import com.example.device_service.entity.Device
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : JpaRepository<Device, Long> {
    fun findAllByUserId(userId: Long): List<Device>
}