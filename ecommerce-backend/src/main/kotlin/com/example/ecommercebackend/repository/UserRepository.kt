package com.example.ecommercebackend.repository

import com.example.ecommercebackend.model.User
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, String> {
    suspend fun findByEmail(email: String): User?
    fun findByFullNameContainingIgnoreCase(fullName: String): Flow<User>
    suspend fun existsByEmail(email: String): Boolean
}