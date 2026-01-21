package com.example.user_service.controller

import com.example.user_service.dto.UserDTO
import com.example.user_service.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(@RequestBody userDTO: UserDTO): ResponseEntity<UserDTO> {
        val created = userService.createUser(userDTO)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created)

    }

    @GetMapping("/{id}")
    fun getUserByID(@PathVariable id: Long): ResponseEntity<UserDTO> =
        userService.getUserByID(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody userDto: UserDTO
    ): ResponseEntity<String> {
        userService.updateUser(id, userDto)
        return ResponseEntity.ok("User updated successfully")
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}