package com.example.ecommercebackend.exception

import org.springframework.http.HttpStatus

class InsufficientStockException(message: String) : AppException(HttpStatus.BAD_REQUEST, message)