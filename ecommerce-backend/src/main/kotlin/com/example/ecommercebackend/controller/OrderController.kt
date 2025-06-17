package com.example.ecommercebackend.controller

import com.example.ecommercebackend.dto.CreateOrderRequest
import com.example.ecommercebackend.dto.OrderResponseDto
import com.example.ecommercebackend.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    suspend fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponseDto> {
        val createdOrder = orderService.createOrder(request)
        val (order, items) = orderService.getOrderDetails(createdOrder.id!!)!!
        val responseDto = order.toResponseDto(items)
        return ResponseEntity.created(URI.create("/api/orders/${order.id}")).body(responseDto)
    }

    @GetMapping("/{id}")
    suspend fun getOrderById(@PathVariable id: Long): ResponseEntity<OrderResponseDto> {
        val orderDetails = orderService.getOrderDetails(id)
            ?: throw NoSuchElementException("Order with ID $id not found")

        val (order, items) = orderDetails
        return ResponseEntity.ok(order.toResponseDto(items))
    }
}