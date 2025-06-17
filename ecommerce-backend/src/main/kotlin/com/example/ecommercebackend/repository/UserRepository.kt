package com.example.ecommercebackend.repository

import com.example.ecommercebackend.model.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, String>