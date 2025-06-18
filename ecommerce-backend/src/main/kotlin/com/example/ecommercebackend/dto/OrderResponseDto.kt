package com.example.ecommercebackend.dto

import java.math.BigDecimal

data class OrderResponseDto(
    val orderId: String,
    val userId: String,
    val status: String,
    val totalAmount: BigDecimal,
    val items: List<OrderItemResponseDto>
)