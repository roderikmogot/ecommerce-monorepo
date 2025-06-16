package com.example.ecommercebackend.controller

import com.example.ecommercebackend.dto.ProductDto
import com.example.ecommercebackend.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class Product(private val productRepository: ProductRepository) {

    @GetMapping
    fun getAllProducts(): Flow<ProductDto> {
        return productRepository.findAll().map { it.toDto() }
    }

    @GetMapping("/{id}")
    suspend fun getProductById(@PathVariable id: Long): ResponseEntity<ProductDto> {
        val product = productRepository.findById(id)
        return product?.let {
            ResponseEntity.ok(it.toDto())
        } ?: ResponseEntity.notFound().build()
    }
}