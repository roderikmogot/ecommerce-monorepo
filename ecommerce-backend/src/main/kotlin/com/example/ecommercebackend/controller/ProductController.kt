package com.example.ecommercebackend.controller

import com.example.ecommercebackend.dto.ProductDto
import com.example.ecommercebackend.service.ProductService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getAllProducts(): Flow<ProductDto> {
        return productService.findAll().map { it.toDto() }
    }

    @GetMapping("/{id}")
    suspend fun getProductById(@PathVariable id: Long): ResponseEntity<ProductDto> {
        val product = productService.findById(id)
        return product?.let {
            ResponseEntity.ok(it.toDto())
        } ?: ResponseEntity.notFound().build()
    }

    // TODO tambahin add product
}