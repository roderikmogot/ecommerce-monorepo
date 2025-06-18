package com.example.ecommercebackend.exception.general

import org.springframework.http.HttpStatus

class ProductAlreadyExistsException(name: String) : AppException(HttpStatus.CONFLICT, "Product already exists: $name")