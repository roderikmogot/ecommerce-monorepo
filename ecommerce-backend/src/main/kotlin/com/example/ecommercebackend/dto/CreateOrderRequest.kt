package com.example.ecommercebackend.dto

data class CreateOrderRequest(
    val userId: Long,
    val items: List<CartItemDto>
)