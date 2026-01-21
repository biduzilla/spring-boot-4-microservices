package com.example.device_service.service

import com.example.device_service.dto.DeviceDto
import com.example.device_service.entity.toDto
import com.example.device_service.exception.DeviceNotFoundException
import com.example.device_service.repository.DeviceRepository
import com.example.device_service.dto.toDevice
import org.springframework.stereotype.Service

@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
) {
    fun getDeviceById(id: Long): DeviceDto =
        deviceRepository.findById(id)
            .orElseThrow {
                DeviceNotFoundException("Device with id $id not found")
            }.toDto()

    fun createDevice(input: DeviceDto): DeviceDto {
        return deviceRepository.save(input.toDevice()).toDto()
    }

    fun updateDevice(id: Long, input: DeviceDto): DeviceDto =
        deviceRepository.findById(id)
            .orElseThrow { DeviceNotFoundException("Device not found with id $id") }
            .apply {
                name = input.name
                type = input.type
                location = input.location
                userId = input.userId
            }
            .let(deviceRepository::save)
            .toDto()

    fun deleteDevice(id: Long) {
        if (!deviceRepository.existsById(id)) {
            throw DeviceNotFoundException("Device not found with id $id")
        }
        deviceRepository.deleteById(id)
    }

    fun getAllDevicesByUserId(userId: Long): List<DeviceDto> =
        deviceRepository.findAllByUserId(userId).map { it.toDto() }
}