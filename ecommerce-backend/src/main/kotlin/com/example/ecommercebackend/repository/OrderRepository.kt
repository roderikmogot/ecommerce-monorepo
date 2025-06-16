package com.example.ecommercebackend.repository

import com.example.ecommercebackend.model.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderRepository : CoroutineCrudRepository<Order, Long>