package com.example.ecommercebackend.exception

import org.springframework.http.HttpStatus

class InvalidDataException(id: String) : AppException(HttpStatus.BAD_REQUEST, "Invalid Data for User ID: $id")