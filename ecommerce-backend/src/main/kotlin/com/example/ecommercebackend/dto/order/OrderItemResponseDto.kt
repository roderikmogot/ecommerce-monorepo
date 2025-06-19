package com.example.ecommercebackend.dto.order

import com.example.ecommercebackend.model.Order
import com.example.ecommercebackend.model.OrderItem

fun Order.toResponseDto(items: List<OrderItem>): OrderResponseDto {
    return OrderResponseDto(
        id = this.id!!,
        userId = this.userId,
        status = this.status,
        totalAmount = this.totalAmount,
        createdAt = this.createdAt,
        items = items.map {
            OrderItemResponseDto(
                productId = it.productId,
                quantity = it.quantity,
                pricePerItem = it.pricePerItem
            )
        }
    )
}