package com.example.ecommercebackend.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("order_items")
data class OrderItem(
    @Id
    val id: String? = null,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val pricePerItem: BigDecimal
)