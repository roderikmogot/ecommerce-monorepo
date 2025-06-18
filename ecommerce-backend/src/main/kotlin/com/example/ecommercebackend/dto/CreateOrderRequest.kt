package com.example.ecommercebackend.dto

data class CreateOrderRequest(
    val userId: String,
    val items: List<CartItemDto>
)