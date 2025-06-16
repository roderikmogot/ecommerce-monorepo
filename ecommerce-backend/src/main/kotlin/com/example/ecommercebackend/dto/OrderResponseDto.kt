package com.example.ecommercebackend.dto

import java.math.BigDecimal

data class OrderResponseDto(
    val orderId: Long,
    val userId: Long,
    val status: String,
    val totalAmount: BigDecimal,
    val items: List<OrderItemResponseDto>
)