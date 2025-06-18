package com.example.ecommercebackend.dto

import java.math.BigDecimal

data class OrderItemResponseDto(
    val productId: String,
    val quantity: Int,
    val pricePerItem: BigDecimal
)