package com.example.ecommercebackend.controllers

import com.example.ecommercebackend.dto.CreateOrderRequest
import com.example.ecommercebackend.dto.ErrorResponse
import com.example.ecommercebackend.dto.OrderResponseDto
import com.example.ecommercebackend.dto.toResponseDto
import com.example.ecommercebackend.services.InsufficientStockException
import com.example.ecommercebackend.services.OrderService
import com.example.ecommercebackend.services.ProductNotFoundException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    suspend fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<Any> {
        return try {
            val createdOrder = orderService.createOrder(request)
            val (order, items) = orderService.getOrderDetails(createdOrder.id!!)!!
            val responseDto = order.toResponseDto(items)
            ResponseEntity.created(URI.create("/api/orders/${order.id}")).body(responseDto)
        } catch (e: InsufficientStockException) {
            ResponseEntity.badRequest().body(ErrorResponse(e.message ?: "Insufficient stock"))
        } catch (e: ProductNotFoundException) {
            ResponseEntity.badRequest().body(ErrorResponse(e.message ?: "Product not found"))
        } catch (e: OptimisticLockingFailureException) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse("High demand for this item. Please try again."))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse("An internal error occurred."))
        }
    }

    @GetMapping("/{id}")
    suspend fun getOrderById(@PathVariable id: Long): ResponseEntity<OrderResponseDto> {
        val orderDetails = orderService.getOrderDetails(id)
        return orderDetails?.let { (order, items) ->
            ResponseEntity.ok(order.toResponseDto(items))
        } ?: ResponseEntity.notFound().build()
    }
}