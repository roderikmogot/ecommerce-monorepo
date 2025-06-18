package com.example.ecommercebackend.model

import com.example.ecommercebackend.dto.UserResponseDto
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("users")
data class User(
    @Id val id: String? = null,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val createdAt: Instant? = Instant.now(),
) {
    fun toResponseDto(): UserResponseDto {
        val id = this.id ?: throw IllegalStateException("User ID cannot be null")
        return UserResponseDto(
            id = id,
            email = this.email,
            fullName = this.fullName,
            createdAt = this.createdAt ?: Instant.now()
        )
    }
}