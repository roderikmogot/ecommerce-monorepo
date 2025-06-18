package com.example.ecommercebackend.exception.general

import org.springframework.http.HttpStatus

class UserNotFoundException(id: String) : AppException(HttpStatus.NOT_FOUND, "User not found: $id")