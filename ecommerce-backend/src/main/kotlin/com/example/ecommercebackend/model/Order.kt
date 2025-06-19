package com.example.ecommercebackend.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("orders")
data class Order(
    @Id
    val id: String? = null,
    val userId: String,
    val status: String,
    val totalAmount: BigDecimal,
    val createdAt: Instant = Instant.now()
)