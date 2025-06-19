package com.example.ecommercebackend.controller

import com.example.ecommercebackend.dto.product.ProductResponseDto
import com.example.ecommercebackend.dto.product.ProductRegistrationDto
import com.example.ecommercebackend.dto.product.ProductUpdateDto
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

    @PutMapping("/{id}")
    suspend fun updateProduct(
        @PathVariable id: String,
        @Valid @RequestBody updateDto: ProductUpdateDto
    ): ProductResponseDto {
        logger.info { "Processing update request for user: $id" }
        return productService.updateProduct(id, updateDto)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteProduct(@PathVariable id: String) {
        logger.info { "Processing delete request for user: $id" }
        productService.deleteProduct(id)
    }

    @GetMapping("/search")
    fun searchProducts(@RequestParam name: String): Flow<ProductResponseDto> {
        logger.info { "Searching users by name: $name" }
        return productService.searchProductsByName(name)
    }
}