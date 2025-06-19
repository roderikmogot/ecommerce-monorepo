package com.example.ecommercebackend.dto.user

import java.time.Instant

data class UserResponseDto(
    val id: String,
    val email: String,
    val fullName: String,
    val createdAt: Instant
)