package com.example.ecommercebackend.service

import com.example.ecommercebackend.dto.ProductRegistrationDto
import com.example.ecommercebackend.dto.ProductResponseDto
import com.example.ecommercebackend.exception.general.InvalidDataException
import com.example.ecommercebackend.exception.general.ProductAlreadyExistsException
import com.example.ecommercebackend.model.Product
import com.example.ecommercebackend.repository.ProductRepository
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class ProductService (
    val productRepository: ProductRepository,
    private val logger: KLogger
) {

    suspend fun register(registrationDto: ProductRegistrationDto): ProductResponseDto {
        if (productRepository.existsByName(registrationDto.name)) {
            logger.warn { "Product name has been registered: ${registrationDto.name}" }
            throw ProductAlreadyExistsException(registrationDto.name)
        }

        val newProduct = Product(
            name = registrationDto.name,
            description = registrationDto.description,
            price = registrationDto.price,
            stockQuantity = registrationDto.stockQuantity
        )

        val savedProduct = productRepository.save(newProduct)
        logger.info { "New product registered: ${savedProduct.name} with id : ${savedProduct.id}" }

        return savedProduct.toResponseDto()
    }

    fun findAll(): Flow<Product> {
        return productRepository.findAll()
    }

    suspend fun findById(id: String): Product? {
        return productRepository.findById(id)
    }
}