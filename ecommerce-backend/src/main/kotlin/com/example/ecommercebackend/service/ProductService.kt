package com.example.ecommercebackend.service

import com.example.ecommercebackend.dto.product.ProductRegistrationDto
import com.example.ecommercebackend.dto.product.ProductResponseDto
import com.example.ecommercebackend.dto.product.ProductUpdateDto
import com.example.ecommercebackend.exception.general.InvalidDataException
import com.example.ecommercebackend.exception.general.ProductAlreadyExistsException
import com.example.ecommercebackend.exception.general.ProductNotFoundException
import com.example.ecommercebackend.model.Product
import com.example.ecommercebackend.repository.ProductRepository
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    suspend fun updateProduct(id: String, updateDto: ProductUpdateDto): ProductResponseDto {
        val existingProduct = productRepository.findById(id) ?: throw ProductNotFoundException(id)

        if (updateDto.name != null && updateDto.name != existingProduct.name && productRepository.existsByName(updateDto.name)) {
            logger.warn { "Product ${existingProduct.id} attempted to update to an existing name: ${updateDto.name}" }
            throw ProductAlreadyExistsException(updateDto.name)
        } else if (updateDto.name?.isBlank() == true || updateDto.description?.isBlank() == true) {
            logger.warn { "Product ${existingProduct.id} attempted to use blank field" }
            throw InvalidDataException("Product ${existingProduct.id} attempted to use blank field")
        }

        val updatedProduct = existingProduct.copy(
            name = updateDto.name ?: existingProduct.name,
            description = updateDto.description ?: existingProduct.description,
            price = updateDto.price ?: existingProduct.price,
            stockQuantity = updateDto.stockQuantity ?: existingProduct.stockQuantity
        )

        val savedProduct = productRepository.save(updatedProduct)
        logger.info { "Product updated: ${savedProduct.id}" }

        return savedProduct.toResponseDto()
    }

    suspend fun deleteProduct(id: String) {
        logger.debug { "Attempting to delete product: $id" }
        if (!productRepository.existsById(id)) {
            throw ProductNotFoundException(id)
        }
        productRepository.deleteById(id)
        logger.info { "Product deleted: $id" }
    }

    fun searchProductsByName(name: String): Flow<ProductResponseDto> {
        logger.debug { "Searching products by name: $name" }
        return productRepository.findByNameContainingIgnoreCase(name).map { it.toResponseDto() }
    }
}