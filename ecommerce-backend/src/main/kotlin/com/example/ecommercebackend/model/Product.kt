package com.example.ecommercebackend.model

import com.example.ecommercebackend.dto.ProductDto
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("products")
data class Product(
    @Id val id: Long? = null,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockQuantity: Int,
    @Version val version: Long = 0,
    val createdAt: Instant? = Instant.now()
) {
    fun toDto() = ProductDto(
        id = this.id!!,
        name = this.name,
        description = this.description,
        price = this.price,
        stockQuantity = this.stockQuantity
    )
}