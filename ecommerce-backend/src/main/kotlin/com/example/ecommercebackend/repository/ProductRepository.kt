package com.example.ecommercebackend.repository

import com.example.ecommercebackend.model.Product
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<Product, Long>