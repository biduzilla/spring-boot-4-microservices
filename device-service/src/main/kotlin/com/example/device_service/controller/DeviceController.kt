package com.example.device_service.controller

import com.example.device_service.dto.DeviceDto
import com.example.device_service.service.DeviceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/device")
class DeviceController(
    private val deviceService: DeviceService
) {
    @GetMapping("/{id}")
    fun getDeviceById(@PathVariable id: Long): ResponseEntity<DeviceDto> =
        ResponseEntity.ok(deviceService.getDeviceById(id))

    @PostMapping("/create")
    fun createDevice(@RequestBody deviceDto: DeviceDto): ResponseEntity<DeviceDto> =
        ResponseEntity.ok(deviceService.createDevice(deviceDto))

    @PutMapping("/{id}")
    fun updateDevice(
        @PathVariable id: Long,
        @RequestBody deviceDto: DeviceDto
    ): ResponseEntity<DeviceDto> =
        ResponseEntity.ok(deviceService.updateDevice(id, deviceDto))

    @DeleteMapping("/{id}")
    fun deleteDevice(@PathVariable id: Long): ResponseEntity<Void> {
        deviceService.deleteDevice(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/user/{userId}")
    fun getAllDevicesByUserId(@PathVariable userId: Long): ResponseEntity<List<DeviceDto>> =
        ResponseEntity.ok(deviceService.getAllDevicesByUserId(userId))

}