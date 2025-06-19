package com.example.ecommercebackend.controller

import com.example.ecommercebackend.dto.product.ProductRegistrationDto
import com.example.ecommercebackend.dto.product.ProductResponseDto
import com.example.ecommercebackend.dto.product.ProductUpdateDto
import com.example.ecommercebackend.exception.GlobalExceptionHandler
import com.example.ecommercebackend.exception.general.InvalidDataException
import com.example.ecommercebackend.exception.general.ProductAlreadyExistsException
import com.example.ecommercebackend.exception.general.ProductNotFoundException
import com.example.ecommercebackend.model.Product
import com.example.ecommercebackend.service.ProductService
import io.github.oshai.kotlinlogging.KLogger
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.math.BigDecimal
import java.time.Instant

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class ProductControllerTest {
    private val productService = mockk<ProductService>()
    private val logger = mockk<KLogger>(relaxed = true)
    private lateinit var controller: ProductController
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup() {
        controller = ProductController(productService, logger)
        webTestClient = WebTestClient
            .bindToController(controller)
            .controllerAdvice(GlobalExceptionHandler(logger))
            .build()
    }

    @Test
    fun `register should return 201 when product registration is successful`() = runTest {
        val registrationDto = ProductRegistrationDto(
            name = "Test Product",
            description = "Test Description",
            price = BigDecimal("99"),
            stockQuantity = 10
        )

        val responseDto = ProductResponseDto(
            id = "product-123",
            name = registrationDto.name,
            description = registrationDto.description,
            price = registrationDto.price,
            stockQuantity = registrationDto.stockQuantity
        )

        coEvery { productService.register(registrationDto) } returns responseDto

        webTestClient.post()
            .uri("/api/products/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(registrationDto))
            .exchange()
            .expectStatus().isCreated
            .expectBody(ProductResponseDto::class.java)
            .isEqualTo(responseDto)

        coVerify { productService.register(registrationDto) }
    }

    @Test
    fun `register should return 409 when product name already exists`() = runTest {
        val registrationDto = ProductRegistrationDto(
            name = "Existing Product",
            description = "Test Description",
            price = BigDecimal("99.99"),
            stockQuantity = 10
        )

        coEvery {
            productService.register(registrationDto)
        } throws ProductAlreadyExistsException(registrationDto.name)

        webTestClient.post()
            .uri("/api/products/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(registrationDto))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.status").isEqualTo(409)
            .jsonPath("$.error").isEqualTo("Product Already Exists")
            .jsonPath("$.message").isEqualTo("Product already exists: ${registrationDto.name}")

        coVerify { productService.register(registrationDto) }
    }

    @Test
    fun `getProductById should return 200 and product when product exists`() = runTest {
        val productId = "product-123"
        val product = Product(
            id = productId,
            name = "Test Product",
            description = "Test Description",
            price = BigDecimal("99.99"),
            stockQuantity = 10
        )

        coEvery { productService.findById(productId) } returns product

        webTestClient.get()
            .uri("/api/products/$productId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(productId)
            .jsonPath("$.name").isEqualTo(product.name)
            .jsonPath("$.description").isEqualTo(product.description.toString())
            .jsonPath("$.price").isEqualTo(99.99)
            .jsonPath("$.stockQuantity").isEqualTo(product.stockQuantity)

        coVerify { productService.findById(productId) }
    }

    @Test
    fun `getProductById should return 404 when product doesn't exist`() = runTest {
        val productId = "nonexistent-product"

        coEvery { productService.findById(productId) } returns null

        webTestClient.get()
            .uri("/api/products/$productId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound

        coVerify { productService.findById(productId) }
    }

    @Test
    fun `getAllProducts should return 200 and all products`() = runTest {
        val products = listOf(
            Product(
                id = "product-1",
                name = "Product One",
                description = "Description One",
                price = BigDecimal("99.99"),
                stockQuantity = 10
            ),
            Product(
                id = "product-2",
                name = "Product Two",
                description = "Description Two",
                price = BigDecimal("149.99"),
                stockQuantity = 20
            )
        )

        every { productService.findAll() } returns flowOf(*products.toTypedArray())

        webTestClient.get()
            .uri("/api/products")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(ProductResponseDto::class.java)
            .hasSize(2)
            .contains(products[0].toResponseDto())
            .contains(products[1].toResponseDto())

        coVerify { productService.findAll() }
    }

    @Test
    fun `updateProduct should return 200 when update is successful`() = runTest {
        val productId = "product-123"
        val updateDto = ProductUpdateDto(
            name = "Updated Product",
            description = "Updated Description",
            price = BigDecimal("129.99"),
            stockQuantity = 15
        )

        val responseDto = ProductResponseDto(
            id = productId,
            name = updateDto.name!!,
            description = updateDto.description!!,
            price = updateDto.price!!,
            stockQuantity = updateDto.stockQuantity!!
        )

        coEvery { productService.updateProduct(productId, updateDto) } returns responseDto

        webTestClient.put()
            .uri("/api/products/$productId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(updateDto))
            .exchange()
            .expectStatus().isOk
            .expectBody(ProductResponseDto::class.java)
            .isEqualTo(responseDto)

        coVerify { productService.updateProduct(productId, updateDto) }
    }

    @Test
    fun `updateProduct should return 404 when product doesn't exist`() = runTest {
        val productId = "nonexistent-product"
        val updateDto = ProductUpdateDto(
            name = "Updated Product",
            description = "Updated Description",
            price = BigDecimal("129.99"),
            stockQuantity = 15
        )

        coEvery {
            productService.updateProduct(productId, updateDto)
        } throws ProductNotFoundException(productId)

        webTestClient.put()
            .uri("/api/products/$productId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(updateDto))
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Product Not Found")
            .jsonPath("$.message").isEqualTo("Product not found: $productId")

        coVerify { productService.updateProduct(productId, updateDto) }
    }

    @Test
    fun `updateProduct should return 409 when updated name already exists`() = runTest {
        val productId = "product-123"
        val updateDto = ProductUpdateDto(
            name = "Existing Product",
            description = "Updated Description",
            price = BigDecimal("129.99"),
            stockQuantity = 15
        )

        coEvery {
            productService.updateProduct(productId, updateDto)
        } throws ProductAlreadyExistsException(updateDto.name!!)

        webTestClient.put()
            .uri("/api/products/$productId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(updateDto))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.status").isEqualTo(409)
            .jsonPath("$.error").isEqualTo("Product Already Exists")
            .jsonPath("$.message").isEqualTo("Product already exists: ${updateDto.name}")

        coVerify { productService.updateProduct(productId, updateDto) }
    }

    @Test
    fun `updateProduct should return 400 when data is invalid`() = runTest {
        val productId = "product-123"
        val updateDto = ProductUpdateDto(
            name = "",
            description = "Updated Description",
            price = BigDecimal("129.99"),
            stockQuantity = 15
        )

        coEvery {
            productService.updateProduct(productId, updateDto)
        } throws InvalidDataException("Product attempted to use blank field")

        webTestClient.put()
            .uri("/api/products/$productId")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(updateDto))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.error").isEqualTo("Bad Request")
            .jsonPath("$.message").isEqualTo("Product attempted to use blank field")

        coVerify { productService.updateProduct(productId, updateDto) }
    }

    @Test
    fun `deleteProduct should return 204 when deletion is successful`() = runTest {
        val productId = "product-123"

        coEvery { productService.deleteProduct(productId) } just io.mockk.runs

        webTestClient.delete()
            .uri("/api/products/$productId")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty

        coVerify { productService.deleteProduct(productId) }
    }

    @Test
    fun `deleteProduct should return 404 when product doesn't exist`() = runTest {
        val productId = "nonexistent-product"

        coEvery {
            productService.deleteProduct(productId)
        } throws ProductNotFoundException(productId)

        webTestClient.delete()
            .uri("/api/products/$productId")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Product Not Found")
            .jsonPath("$.message").isEqualTo("Product not found: $productId")

        coVerify { productService.deleteProduct(productId) }
    }

    @Test
    fun `searchProducts should return 200 and matching products`() = runTest {
        val searchTerm = "test"
        val products = listOf(
            ProductResponseDto(
                id = "product-1",
                name = "Test Product",
                description = "Description One",
                price = BigDecimal("99.99"),
                stockQuantity = 10
            ),
            ProductResponseDto(
                id = "product-2",
                name = "Another Test Product",
                description = "Description Two",
                price = BigDecimal("149.99"),
                stockQuantity = 20
            )
        )

        every { productService.searchProductsByName(searchTerm) } returns products.asFlow()

        webTestClient.get()
            .uri("/api/products/search?name=$searchTerm")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(ProductResponseDto::class.java)
            .hasSize(2)
            .contains(products[0])
            .contains(products[1])

        coVerify { productService.searchProductsByName(searchTerm) }
    }
}