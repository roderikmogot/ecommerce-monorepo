package com.example.ecommercebackend.dto.order

data class CreateOrderRequest(
    val userId: String,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productId: String,
    val quantity: Int
)