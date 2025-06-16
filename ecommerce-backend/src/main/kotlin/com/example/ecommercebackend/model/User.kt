package com.example.ecommercebackend.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("users")
data class User(
    @Id val id: String,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val createdAt: Instant? = Instant.now(),
)