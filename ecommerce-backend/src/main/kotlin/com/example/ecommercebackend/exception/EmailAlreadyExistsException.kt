package com.example.ecommercebackend.exception

import org.springframework.http.HttpStatus

class EmailAlreadyExistsException(email: String) : AppException(HttpStatus.CONFLICT, "Email already exists: $email")
