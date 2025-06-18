package com.example.ecommercebackend.exception

import org.springframework.http.HttpStatus

sealed class AppException(
    val httpStatus: HttpStatus,
    override val message: String
) : RuntimeException(message)
