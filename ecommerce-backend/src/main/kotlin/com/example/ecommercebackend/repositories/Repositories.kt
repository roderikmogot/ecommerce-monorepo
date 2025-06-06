package com.example.ecommercebackend.repositories

import com.example.ecommercebackend.models.Order
import com.example.ecommercebackend.models.OrderItem
import com.example.ecommercebackend.models.Product

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<Product, Long>

interface OrderRepository : CoroutineCrudRepository<Order, Long>

interface OrderItemRepository : CoroutineCrudRepository<OrderItem, Long> {
    fun findByOrderId(orderId: Long): Flow<OrderItem>
}