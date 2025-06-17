package com.example.ecommercebackend.service

import com.example.ecommercebackend.exception.UserNotFoundException
import com.example.ecommercebackend.model.User
import com.example.ecommercebackend.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserService (private val userRepository: UserRepository) {
    suspend fun save(user: User): User = userRepository.save(user)
    suspend fun findById(id: String): User = userRepository.findById(id) ?: throw UserNotFoundException("User not found: $id")
    fun findAll(): Flow<User> = userRepository.findAll()
}