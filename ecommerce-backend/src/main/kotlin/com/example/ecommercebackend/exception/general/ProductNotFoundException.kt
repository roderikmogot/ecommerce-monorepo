package com.example.ecommercebackend.exception.general

import org.springframework.http.HttpStatus

class ProductNotFoundException(id: String) : AppException(HttpStatus.NOT_FOUND, "Product not found: $id")