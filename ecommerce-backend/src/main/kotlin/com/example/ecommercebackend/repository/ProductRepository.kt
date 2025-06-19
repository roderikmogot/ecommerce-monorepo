package com.example.ecommercebackend.repository

import com.example.ecommercebackend.model.Product
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<Product, String> {
    suspend fun existsByName(name: String): Boolean
    abstract fun findByNameContainingIgnoreCase(name: String): Flow<Product>
}