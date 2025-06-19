package com.example.ecommercebackend.dto.product

import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

data class ProductUpdateDto (
    val name: String?,
    val description: String?,
    @field:PositiveOrZero(message = "Price should be positive.")
    val price: BigDecimal?,
    @field:PositiveOrZero(message = "Stock Quantity should be positive.")
    val stockQuantity: Int?,
)