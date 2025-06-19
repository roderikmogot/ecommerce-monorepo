package com.example.ecommercebackend.dto.order

import java.math.BigDecimal
import java.time.Instant

data class OrderResponseDto(
    val id: String,
    val userId: String,
    val status: String,
    val totalAmount: BigDecimal,
    val createdAt: Instant,
    val items: List<OrderItemResponseDto>
)

data class OrderItemResponseDto(
    val productId: String,
    val quantity: Int,
    val pricePerItem: BigDecimal
)