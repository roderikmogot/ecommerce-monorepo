package com.example.ecommercebackend.exception.general

import org.springframework.http.HttpStatus

class EmailAlreadyExistsException(email: String) : AppException(HttpStatus.CONFLICT, "Email already exists: $email")
