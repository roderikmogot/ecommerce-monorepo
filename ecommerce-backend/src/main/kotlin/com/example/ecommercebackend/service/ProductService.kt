package com.example.ecommercebackend.service

import com.example.ecommercebackend.model.Product
import com.example.ecommercebackend.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ProductService (val productRepository: ProductRepository) {
    fun findAll(): Flow<Product> {
        return productRepository.findAll()
    }

    suspend fun findById(id: Long): Product? {
        return productRepository.findById(id)
    }
}