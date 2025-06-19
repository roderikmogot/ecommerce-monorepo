package com.example.ecommercebackend.dto.product

import jakarta.validation.constraints.*
import java.math.BigDecimal

data class ProductRegistrationDto (
    @field:NotBlank(message = "Name of the product is required")
    val name: String,
    val description: String?,
    @field:PositiveOrZero(message = "Price should be positive.")
    val price: BigDecimal,
    @field:PositiveOrZero(message = "Stock Quantity should be positive.")
    val stockQuantity: Int,
)