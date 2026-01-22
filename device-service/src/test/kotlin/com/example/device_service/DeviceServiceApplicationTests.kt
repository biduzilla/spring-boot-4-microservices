package com.example.device_service

import com.example.device_service.entity.Device
import com.example.device_service.model.DeviceType
import com.example.device_service.repository.DeviceRepository
import com.example.device_service.utils.logger
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DeviceServiceApplicationTests(
    @Autowired
    private val deviceRepository: DeviceRepository,
) {
    companion object{
        val log = logger()
        val NUMBER_OF_DEVICES = 200
        val USERS = 10
    }

    @Test
    fun contextLoads() {
    }

    @Test
    fun createDevices() {
        repeat(NUMBER_OF_DEVICES) { i ->
            deviceRepository.save(
                Device(
                    name = "Device${i + 1}",
                    type = DeviceType.entries[(i + 1) % DeviceType.entries.size],
                    location = "Location${(i + 1) % 3 + 1}",
                    userId = ((i + 1) % USERS + 1).toLong()
                )
            )
        }

        log.info("Device Repository has been populated")
    }
}
