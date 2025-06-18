package com.example.ecommercebackend.exception

import org.springframework.http.HttpStatus

class UserNotFoundException(id: String) : AppException(HttpStatus.NOT_FOUND, "User not found: $id")