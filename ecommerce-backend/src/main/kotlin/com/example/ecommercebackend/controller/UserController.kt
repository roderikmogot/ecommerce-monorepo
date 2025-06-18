package com.example.ecommercebackend.controller

import com.example.ecommercebackend.dto.UserRegistrationDto
import com.example.ecommercebackend.dto.UserResponseDto
import com.example.ecommercebackend.dto.UserUpdateDto
import com.example.ecommercebackend.service.UserService
import io.github.oshai.kotlinlogging.KLogger
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val logger: KLogger
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun register(@Valid @RequestBody registrationDto: UserRegistrationDto): UserResponseDto {
        logger.info { "Processing registration request for: ${registrationDto.email}" }
        return userService.register(registrationDto)
    }

    @GetMapping
    fun getAllUsers(): Flow<UserResponseDto> {
        logger.info { "Retrieving all users" }
        return userService.getAllUsers()
    }

    @GetMapping("/{id}")
    suspend fun getUserById(@PathVariable id: String): UserResponseDto {
        logger.info { "Retrieving user by ID: $id" }
        return userService.getUserById(id)
    }

    @PutMapping("/{id}")
    suspend fun updateUser(
        @PathVariable id: String,
        @Valid @RequestBody updateDto: UserUpdateDto
    ): UserResponseDto {
        logger.info { "Processing update request for user: $id" }
        return userService.updateUser(id, updateDto)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteUser(@PathVariable id: String) {
        logger.info { "Processing delete request for user: $id" }
        userService.deleteUser(id)
    }

    @GetMapping("/search")
    fun searchUsers(@RequestParam name: String): Flow<UserResponseDto> {
        logger.info { "Searching users by name: $name" }
        return userService.searchUsersByName(name)
    }

    @GetMapping("/by-email")
    suspend fun getUserByEmail(@RequestParam email: String): UserResponseDto? {
        logger.info { "Retrieving user by email: $email" }
        return userService.findByEmail(email)
    }
}