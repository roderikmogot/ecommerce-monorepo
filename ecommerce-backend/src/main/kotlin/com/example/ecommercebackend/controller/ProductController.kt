package com.example.ecommercebackend.controller

import com.example.ecommercebackend.dto.ProductResponseDto
import com.example.ecommercebackend.dto.ProductRegistrationDto
import com.example.ecommercebackend.service.ProductService
import io.github.oshai.kotlinlogging.KLogger
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService,
    private val logger: KLogger
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun register(@Valid @RequestBody registrationDto: ProductRegistrationDto): ProductResponseDto {
        logger.info { "Product registration for: ${registrationDto.name}" }
        return productService.register(registrationDto)
    }

    @GetMapping
    fun getAllProducts(): Flow<ProductResponseDto> {
        return productService.findAll().map { it.toResponseDto() }
    }

    @GetMapping("/{id}")
    suspend fun getProductById(@PathVariable id: String): ResponseEntity<ProductResponseDto> {
        val product = productService.findById(id)
        return product?.let {
            ResponseEntity.ok(it.toResponseDto())
        } ?: ResponseEntity.notFound().build()
    }

    // TODO tambahin add product
}