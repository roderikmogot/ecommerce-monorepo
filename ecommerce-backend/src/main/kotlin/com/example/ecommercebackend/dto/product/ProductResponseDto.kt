package com.example.ecommercebackend.dto.product

import java.math.BigDecimal

data class ProductResponseDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int
)