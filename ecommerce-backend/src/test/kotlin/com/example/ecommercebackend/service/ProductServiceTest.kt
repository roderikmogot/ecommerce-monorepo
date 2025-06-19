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
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class ProductServiceTest {

    private val productRepository = mockk<ProductRepository>()
    private val logger = mockk<KLogger>(relaxed = true)
    private lateinit var productService: ProductService

    @BeforeEach
    fun setup() {
        productService = ProductService(productRepository, logger)
    }

    @Test
    fun `register should create a new product when name doesn't exist`() = runBlocking {

        val registrationDto = ProductRegistrationDto(
            name = "Test Product",
            description = "Test Description",
            price = BigDecimal(100),
            stockQuantity = 10
        )

        val productId = "product-123"
        val savedProduct = Product(
            id = productId,
            name = registrationDto.name,
            description = registrationDto.description,
            price = registrationDto.price,
            stockQuantity = registrationDto.stockQuantity
        )

        coEvery { productRepository.existsByName(registrationDto.name) } returns false
        coEvery {
            productRepository.save(match {
                it.name == registrationDto.name &&
                        it.description == registrationDto.description &&
                        it.price == registrationDto.price &&
                        it.stockQuantity == registrationDto.stockQuantity
            })
        } returns savedProduct

        val result = productService.register(registrationDto)

        assertEquals(productId, result.id)
        assertEquals(registrationDto.name, result.name)
        assertEquals(registrationDto.description, result.description)
        assertEquals(registrationDto.price, result.price)
        assertEquals(registrationDto.stockQuantity, result.stockQuantity)

        coVerify { productRepository.existsByName(registrationDto.name) }
        coVerify { productRepository.save(any()) }
    }

    @Test
    fun `register should throw ProductAlreadyExistsException when name exists`() = runBlocking {

        val registrationDto = ProductRegistrationDto(
            name = "Existing Product",
            description = "Test Description",
            price = BigDecimal(100),
            stockQuantity = 10
        )

        coEvery { productRepository.existsByName(registrationDto.name) } returns true

        assertThrows<ProductAlreadyExistsException> {
            productService.register(registrationDto)
        }

        coVerify { productRepository.existsByName(registrationDto.name) }
        coVerify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `findAll should return all products`() = runBlocking {

        val products = listOf(
            Product(
                id = "product-1",
                name = "Product One",
                description = "Description One",
                price = BigDecimal(100),
                stockQuantity = 10
            ),
            Product(
                id = "product-2",
                name = "Product Two",
                description = "Description Two",
                price = BigDecimal(200),
                stockQuantity = 20
            )
        )

        every { productRepository.findAll() } returns flowOf(*products.toTypedArray())

        val result = productService.findAll()

        val resultList = result.toList()
        assertEquals(2, resultList.size)
        assertEquals(products[0].id, resultList[0].id)
        assertEquals(products[1].id, resultList[1].id)
        verify { productRepository.findAll() }
    }

    @Test
    fun `findById should return product when exists`() = runBlocking {

        val productId = "product-123"
        val product = Product(
            id = productId,
            name = "Test Product",
            description = "Test Description",
            price = BigDecimal(100),
            stockQuantity = 10
        )

        coEvery { productRepository.findById(productId) } returns product

        val result = productService.findById(productId)

        assertEquals(productId, result?.id)
        assertEquals(product.name, result?.name)
        assertEquals(product.description, result?.description)
        assertEquals(product.price, result?.price)
        assertEquals(product.stockQuantity, result?.stockQuantity)
        coVerify { productRepository.findById(productId) }
    }

    @Test
    fun `findById should return null when product doesn't exist`() = runBlocking {

        val productId = "nonexistent-product"

        coEvery { productRepository.findById(productId) } returns null

        val result = productService.findById(productId)

        assertEquals(null, result)
        coVerify { productRepository.findById(productId) }
    }

    @Test
    fun `updateProduct should update and return product when data is valid`() = runBlocking {

        val productId = "product-123"
        val existingProduct = Product(
            id = productId,
            name = "Old Product",
            description = "Old Description",
            price = BigDecimal(100),
            stockQuantity = 10
        )

        val updateDto = ProductUpdateDto(
            name = "Updated Product",
            description = "Updated Description",
            price = BigDecimal(150),
            stockQuantity = 15
        )

        val updatedProduct = existingProduct.copy(
            name = updateDto.name!!,
            description = updateDto.description!!,
            price = updateDto.price!!,
            stockQuantity = updateDto.stockQuantity!!
        )

        coEvery { productRepository.findById(productId) } returns existingProduct
        coEvery { productRepository.existsByName(updateDto.name!!) } returns false
        coEvery { productRepository.save(any()) } returns updatedProduct

        val result = productService.updateProduct(productId, updateDto)

        assertEquals(productId, result.id)
        assertEquals(updateDto.name, result.name)
        assertEquals(updateDto.description, result.description)
        assertEquals(updateDto.price, result.price)
        assertEquals(updateDto.stockQuantity, result.stockQuantity)

        coVerify { productRepository.findById(productId) }
        coVerify { productRepository.existsByName(updateDto.name!!) }
        coVerify { productRepository.save(any()) }
    }

    @Test
    fun `updateProduct should throw ProductNotFoundException when product doesn't exist`() = runBlocking {

        val productId = "nonexistent-product"
        val updateDto = ProductUpdateDto(
            name = "Updated Product",
            description = "Updated Description",
            price = BigDecimal(150),
            stockQuantity = 15
        )

        coEvery { productRepository.findById(productId) } returns null

        assertThrows<ProductNotFoundException> {
            productService.updateProduct(productId, updateDto)
        }

        coVerify { productRepository.findById(productId) }
        coVerify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun `updateProduct should throw ProductAlreadyExistsException when new name already exists`() = runBlocking {

        val productId = "product-123"
        val existingProduct = Product(
            id = productId,
            name = "Old Product",
            description = "Old Description",
            price = BigDecimal(100),
            stockQuantity = 10
        )

        val updateDto = ProductUpdateDto(
            name = "Existing Product Name",
            description = "Updated Description",
            price = BigDecimal(150),
            stockQuantity = 15
        )

        coEvery { productRepository.findById(productId) } returns existingProduct
        coEvery { productRepository.existsByName(updateDto.name!!) } returns true

        assertThrows<ProductAlreadyExistsException> {
            productService.updateProduct(productId, updateDto)
        }

        coVerify { productRepository.findById(productId) }
        coVerify { productRepository.existsByName(updateDto.name!!) }
        coVerify(exactly = 0) { productRepository.save(any()) }
    }


    @Test
    fun `deleteProduct should delete product when exists`() = runBlocking {

        val productId = "product-123"

        coEvery { productRepository.existsById(productId) } returns true
        coEvery { productRepository.deleteById(productId) } just runs

        productService.deleteProduct(productId)

        coVerify { productRepository.existsById(productId) }
        coVerify { productRepository.deleteById(productId) }
    }

    @Test
    fun `deleteProduct should throw ProductNotFoundException when product doesn't exist`() = runBlocking {

        val productId = "nonexistent-product"

        coEvery { productRepository.existsById(productId) } returns false

        assertThrows<ProductNotFoundException> {
            productService.deleteProduct(productId)
        }

        coVerify { productRepository.existsById(productId) }
        coVerify(exactly = 0) { productRepository.deleteById(any()) }
    }

    @Test
    fun `searchProductsByName should return matching products`() = runBlocking {

        val searchName = "test"
        val products = listOf(
            Product(
                id = "product-1",
                name = "Test Product",
                description = "Description One",
                price = BigDecimal(100),
                stockQuantity = 10
            ),
            Product(
                id = "product-2",
                name = "Another Test Product",
                description = "Description Two",
                price = BigDecimal(200),
                stockQuantity = 20
            )
        )

        every { productRepository.findByNameContainingIgnoreCase(searchName) } returns flowOf(*products.toTypedArray())

        val result = productService.searchProductsByName(searchName)

        val resultList = result.toList()
        assertEquals(2, resultList.size)
        assertEquals(products[0].id, resultList[0].id)
        assertEquals(products[1].id, resultList[1].id)
        verify { productRepository.findByNameContainingIgnoreCase(searchName) }
    }
}