package com.example.device_service.entity

import com.example.device_service.dto.DeviceDto
import com.example.device_service.model.DeviceType
import jakarta.persistence.*

@Entity
@Table(name = "device")
data class Device(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    var type: DeviceType,
    var location: String,
    var userId: Long,
)

fun Device.toDto(): DeviceDto {
    return DeviceDto(
        this.id,
        this.name,
        this.type,
        this.location,
        this.userId,
    )
}
