package com.example.ecommercebackend.dto.order

data class CreateOrderRequest(
    val userId: String,
    val items: List<CartItemDto>
)