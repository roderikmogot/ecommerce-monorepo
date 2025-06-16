package com.example.ecommercebackend.repository

import com.example.ecommercebackend.model.OrderItem
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderItemRepository : CoroutineCrudRepository<OrderItem, Long> {
    fun findByOrderId(orderId: Long): Flow<OrderItem>
}