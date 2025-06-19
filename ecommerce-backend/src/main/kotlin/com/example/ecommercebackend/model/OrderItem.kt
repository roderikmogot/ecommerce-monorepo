package com.example.ecommercebackend.model

import com.example.ecommercebackend.dto.order.OrderItemResponseDto
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("order_items")
data class OrderItem(
    @Id val id: String? = null,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val pricePerItem: BigDecimal
) {
    fun toResponseDto() = OrderItemResponseDto(
        productId = this.productId,
        quantity = this.quantity,
        pricePerItem = this.pricePerItem
    )
}