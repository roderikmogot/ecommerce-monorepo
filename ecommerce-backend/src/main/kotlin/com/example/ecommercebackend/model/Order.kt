package com.example.ecommercebackend.model

import com.example.ecommercebackend.dto.order.OrderResponseDto
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("orders")
data class Order(
    @Id val id: String? = null,
    val userId: String,
    val status: String,
    val totalAmount: BigDecimal,
    val createdAt: Instant? = Instant.now()
) {
    fun toResponseDto(items: List<OrderItem>) = OrderResponseDto(
        orderId = this.id!!,
        userId = this.userId,
        status = this.status,
        totalAmount = this.totalAmount,
        items = items.map { it.toResponseDto() }
    )
}