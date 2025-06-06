package com.example.ecommercebackend.dto

import com.example.ecommercebackend.models.Order
import com.example.ecommercebackend.models.OrderItem
import com.example.ecommercebackend.models.Product
import java.math.BigDecimal

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int
)

fun Product.toDto() = ProductDto(
    id = this.id!!,
    name = this.name,
    description = this.description,
    price = this.price,
    stockQuantity = this.stockQuantity
)

data class CartItemDto(
    val productId: Long,
    val quantity: Int
)

data class CreateOrderRequest(
    val userId: Long,
    val items: List<CartItemDto>
)

data class OrderItemResponseDto(
    val productId: Long,
    val quantity: Int,
    val pricePerItem: BigDecimal
)

data class OrderResponseDto(
    val orderId: Long,
    val userId: Long,
    val status: String,
    val totalAmount: BigDecimal,
    val items: List<OrderItemResponseDto>
)

fun Order.toResponseDto(items: List<OrderItem>) = OrderResponseDto(
    orderId = this.id!!,
    userId = this.userId,
    status = this.status,
    totalAmount = this.totalAmount,
    items = items.map { it.toResponseDto() }
)

fun OrderItem.toResponseDto() = OrderItemResponseDto(
    productId = this.productId,
    quantity = this.quantity,
    pricePerItem = this.pricePerItem
)

data class ErrorResponse(val message: String)