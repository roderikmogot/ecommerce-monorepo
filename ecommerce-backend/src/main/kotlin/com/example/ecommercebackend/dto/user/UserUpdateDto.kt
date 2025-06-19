package com.example.ecommercebackend.dto.user

import jakarta.validation.constraints.Email

data class UserUpdateDto(
    val fullName: String?,

    @field:Email(message = "Email format is invalid")
    val email: String?
)