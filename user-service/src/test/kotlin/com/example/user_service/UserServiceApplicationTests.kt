package com.example.user_service

import com.example.user_service.entity.User
import com.example.user_service.repository.UserRepository
import com.example.user_service.utils.logger
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceApplicationTests(
    @Autowired
    private val userRepository: UserRepository,
) {
    companion object {
        private val log = logger()
        private const val NUMBER_OF_USERS = 10
    }

    @Test
    fun contextLoads() {
    }

    @Disabled
    @Test
    fun addUsersToDB() {
        repeat(NUMBER_OF_USERS) { i ->
            val user = User(
                name = "User$i",
                surname = "Surname$i",
                email = "user$i@example.com",
                address = "$i Example St",
                alerting = i % 2 == 0,
                energyAlertingThreshold = 1000.0 + i
            )

            userRepository.save(user)
        }

        log.info("User Repository populated successfully")
    }

}
