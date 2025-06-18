package com.example.ecommercebackend.exception.general

import org.springframework.http.HttpStatus

class ValidationException(override val message: String) : AppException(HttpStatus.BAD_REQUEST, message)