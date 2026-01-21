package com.example.user_service.service

import com.example.user_service.dto.UserDTO
import com.example.user_service.entity.User
import com.example.user_service.entity.toDTO
import com.example.user_service.repository.UserRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserService(
    private val userRepository: UserRepository
) {

    fun createUser(input: UserDTO): UserDTO {
        val user = User(
            name = input.name,
            surname = input.surname,
            email = input.email,
            address = input.address,
            alerting = input.alerting,
            energyAlertingThreshold = input.energyAlertingThreshold,
        )

        val saved = userRepository.save(user)

        return saved.toDTO()
    }

    fun getUserByID(id: Long): UserDTO? {
        return userRepository.findById(id).map { it.toDTO() }.orElse(null)
    }

    fun updateUser(id: Long, dto: UserDTO) {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }

        user.apply {
            name = dto.name
            surname = dto.surname
            email = dto.email
            address = dto.address
            alerting = dto.alerting
            energyAlertingThreshold = dto.energyAlertingThreshold
        }

        userRepository.save(user)
    }

    fun deleteUser(id: Long) {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }

        userRepository.delete(user)
    }

}