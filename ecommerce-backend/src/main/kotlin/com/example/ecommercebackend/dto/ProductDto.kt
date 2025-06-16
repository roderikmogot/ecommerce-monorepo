package com.example.ecommercebackend.dto

import java.math.BigDecimal

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int
)