package com.example.ecommercebackend.service

import com.example.ecommercebackend.dto.CreateOrderRequest
import com.example.ecommercebackend.exception.*
import com.example.ecommercebackend.model.Order
import com.example.ecommercebackend.model.OrderItem
import com.example.ecommercebackend.repository.OrderItemRepository
import com.example.ecommercebackend.repository.OrderRepository
import com.example.ecommercebackend.repository.ProductRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Service
class OrderService(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository
) {

    @Transactional
    suspend fun createOrder(request: CreateOrderRequest): Order {
        val productIds = request.items.map { it.productId }
        val products = productRepository.findAllById(productIds).toList()
            .associateBy { it.id!! }

        if (products.size != productIds.size) {
            val foundIds = products.keys
            val notFoundIds = productIds.filterNot { it in foundIds }
            throw ProductNotFoundException("Products not found: $notFoundIds")
        }

        var totalAmount = BigDecimal.ZERO

        for (item in request.items) {
            val product = products[item.productId] ?: throw ProductNotFoundException("Products not found: ${item.productId}")
            if (product.stockQuantity < item.quantity) {
                throw InsufficientStockException("Not enough stock for product ${product.name}")
            }
            totalAmount += product.price * BigDecimal(item.quantity)
        }

        val order = orderRepository.save(
            Order(
                userId = request.userId,
                status = "PENDING",
                totalAmount = totalAmount
            )
        )

        for (item in request.items) {
            val product = products[item.productId] ?: throw ProductNotFoundException("Products not found: ${item.productId}")

            orderItemRepository.save(
                OrderItem(
                    orderId = order.id!!,
                    productId = product.id!!,
                    quantity = item.quantity,
                    pricePerItem = product.price
                )
            )

            val updatedProduct = product.copy(stockQuantity = product.stockQuantity - item.quantity)
            productRepository.save(updatedProduct)
        }

        return order.copy(status = "COMPLETED")
    }

    suspend fun getOrderDetails(orderId: Long): Pair<Order, List<OrderItem>>? {
        val order = orderRepository.findById(orderId) ?: return null
        val items = orderItemRepository.findByOrderId(order.id!!).toList()
        return order to items
    }
}