package com.example.ecommercebackend.service

import com.example.ecommercebackend.dto.UserRegistrationDto
import com.example.ecommercebackend.dto.UserResponseDto
import com.example.ecommercebackend.dto.UserUpdateDto
import com.example.ecommercebackend.exception.general.EmailAlreadyExistsException
import com.example.ecommercebackend.exception.general.InvalidDataException
import com.example.ecommercebackend.exception.general.UserNotFoundException
import com.example.ecommercebackend.model.User
import com.example.ecommercebackend.repository.UserRepository
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val logger: KLogger
) {
    suspend fun register(registrationDto: UserRegistrationDto): UserResponseDto {

        if (userRepository.existsByEmail(registrationDto.email)) {
            logger.warn { "Email has been registered: ${registrationDto.email}" }
            throw EmailAlreadyExistsException(registrationDto.email)
        }

        val user = User(
            email = registrationDto.email,
            passwordHash = passwordEncoder.encode(registrationDto.password),
            fullName = registrationDto.fullName,
            createdAt = Instant.now()
        )

        val savedUser = userRepository.save(user)
        logger.info { "New user registered: ${savedUser.id}" }

        return savedUser.toResponseDto()
    }

    suspend fun updateUser(id: String, updateDto: UserUpdateDto): UserResponseDto {
        val existingUser = userRepository.findById(id) ?: throw UserNotFoundException(id)

        if (updateDto.email != null && updateDto.email != existingUser.email && userRepository.existsByEmail(updateDto.email)) {
            logger.warn { "User ${existingUser.id} attempted to update to an existing email: ${updateDto.email}" }
            throw EmailAlreadyExistsException(updateDto.email)
        } else if (updateDto.email?.isBlank() == true || updateDto.fullName?.isBlank() == true) {
            logger.warn { "User ${existingUser.id} attempted to use blank field." }
            throw InvalidDataException("User ${existingUser.id} attempted to use blank field")
        }

        val updatedUser = existingUser.copy(
            email = updateDto.email ?: existingUser.email,
            fullName = updateDto.fullName ?: existingUser.fullName
        )

        val savedUser = userRepository.save(updatedUser)
        logger.info { "User updated: ${savedUser.id}" }

        return savedUser.toResponseDto()
    }

    suspend fun getUserById(id: String): UserResponseDto {
        logger.debug { "Fetching user by ID: $id" }
        val user = userRepository.findById(id)?: throw UserNotFoundException(id)
        return user.toResponseDto()
    }

    fun getAllUsers(): Flow<UserResponseDto> {
        logger.debug { "Fetching all users" }
        return userRepository.findAll().map { it.toResponseDto() }
    }

    suspend fun deleteUser(id: String) {
        logger.debug { "Attempting to delete user: $id" }
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException(id)
        }
        userRepository.deleteById(id)
        logger.info { "User deleted: $id" }
    }

    fun searchUsersByName(name: String): Flow<UserResponseDto> {
        logger.debug { "Searching users by name: $name" }
        return userRepository.findByFullNameContainingIgnoreCase(name).map { it.toResponseDto() }
    }

    suspend fun findByEmail(email: String): UserResponseDto? {
        logger.debug { "Fetching user by email: $email" }
        return userRepository.findByEmail(email)?.toResponseDto()
    }
}