package com.example.ecommercebackend.dto

import java.math.BigDecimal

data class OrderItemResponseDto(
    val productId: Long,
    val quantity: Int,
    val pricePerItem: BigDecimal
)