package com.example.ecommercebackend.controller

import com.example.ecommercebackend.model.User
import com.example.ecommercebackend.service.UserService
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController (private val userService: UserService) {
    @PostMapping
    suspend fun create(@RequestBody user: User): User {
        return userService.save(user)
    }

    @GetMapping
    suspend fun getAll(): Flow<User> {
        return userService.findAll()
    }

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: String): User? {
        return userService.findById(id)
    }
}