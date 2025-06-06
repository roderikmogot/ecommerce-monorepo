package com.example.ecommercebackend.models

import java.math.BigDecimal

import java.time.Instant

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id val id: String,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val createdAt: Instant? = Instant.now(),
)

@Table("products")
data class Product(
    @Id val id: Long? = null,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int,
    @Version val version: Long = 0,
    val createdAt: Instant? = Instant.now()
)

@Table("orders")
data class Order(
    @Id val id: Long? = null,
    val userId: Long,
    val status: String,
    val totalAmount: BigDecimal,
    val createdAt: Instant? = Instant.now()
)

@Table("order_items")
data class OrderItem(
    @Id val id: Long? = null,
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val pricePerItem: BigDecimal
)