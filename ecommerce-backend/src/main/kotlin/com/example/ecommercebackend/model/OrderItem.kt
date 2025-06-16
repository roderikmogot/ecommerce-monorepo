package com.example.ecommercebackend.model

import com.example.ecommercebackend.dto.OrderItemResponseDto
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("order_items")
data class OrderItem(
    @Id val id: Long? = null,
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val pricePerItem: BigDecimal
) {
    fun toResponseDto() = OrderItemResponseDto(
        productId = this.productId,
        quantity = this.quantity,
        pricePerItem = this.pricePerItem
    )
}