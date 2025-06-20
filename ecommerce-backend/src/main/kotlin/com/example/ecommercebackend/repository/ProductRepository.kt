package com.example.ecommercebackend.repository

import com.example.ecommercebackend.model.Product
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<Product, String> {
    suspend fun existsByName(name: String): Boolean
    abstract fun findByNameContainingIgnoreCase(name: String): Flow<Product>

    @Modifying
    @Query("UPDATE product SET stock_quantity = :quantity WHERE id = :id")
    suspend fun updateStockQuantity(id: String, quantity: Int)
}