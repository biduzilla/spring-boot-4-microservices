package com.example.user_service.entity

import com.example.user_service.dto.UserDTO
import jakarta.persistence.*


@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    var name: String = "",
    var surname: String = "",
    var email: String = "",
    var address: String = "",
    var alerting: Boolean = false,
    var energyAlertingThreshold: Double = 0.0
)

fun User.toDTO(): UserDTO {
    return UserDTO(
        id = this.id,
        name = this.name,
        surname = this.surname,
        email = this.email,
        address = this.address,
        alerting = this.alerting,
        energyAlertingThreshold = this.energyAlertingThreshold,
    )
}
