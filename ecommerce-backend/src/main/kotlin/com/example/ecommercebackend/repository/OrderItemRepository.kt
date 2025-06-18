package com.example.ecommercebackend.repository

import com.example.ecommercebackend.model.OrderItem
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderItemRepository : CoroutineCrudRepository<OrderItem, String> {
    fun findByOrderId(orderId: String): Flow<OrderItem>
}