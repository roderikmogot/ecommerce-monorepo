package com.example.ecommercebackend.controller

import com.example.ecommercebackend.dto.UserRegistrationDto
import com.example.ecommercebackend.dto.UserResponseDto
import com.example.ecommercebackend.exception.general.EmailAlreadyExistsException
import com.example.ecommercebackend.exception.GlobalExceptionHandler
import com.example.ecommercebackend.exception.general.UserNotFoundException
import com.example.ecommercebackend.model.User
import com.example.ecommercebackend.service.UserService
import io.github.oshai.kotlinlogging.KLogger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.Instant

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class UserControllerTest {
    private val userService = mockk<UserService>()
    private val logger = mockk<KLogger>(relaxed = true)
    private lateinit var controller: UserController
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup() {
        controller = UserController(userService, logger)
        webTestClient = WebTestClient
            .bindToController(controller)
            .controllerAdvice(GlobalExceptionHandler(logger))
            .build()
    }

    @Test
    fun `register should return 201 when registration is successful`() = runTest {
        val registrationDto = UserRegistrationDto(
            email = "test@example.com",
            password = "password123",
            fullName = "Test User"
        )

        val responseDto = UserResponseDto(
            id = "user-123",
            email = registrationDto.email,
            fullName = registrationDto.fullName,
            createdAt = Instant.now()
        )

        coEvery { userService.register(registrationDto) } returns responseDto

        webTestClient.post()
            .uri("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(registrationDto))
            .exchange()
            .expectStatus().isCreated
            .expectBody(UserResponseDto::class.java)
            .isEqualTo(responseDto)

        coVerify { userService.register(registrationDto) }
    }

    @Test
    fun `register should return 409 when email already exists`() = runTest {
        val registrationDto = UserRegistrationDto(
            email = "existing@example.com",
            password = "password123",
            fullName = "Test User"
        )

        coEvery {
            userService.register(registrationDto)
        } throws EmailAlreadyExistsException(registrationDto.email)

        webTestClient.post()
            .uri("/api/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(registrationDto))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("$.status").isEqualTo(409)
            .jsonPath("$.error").isEqualTo("Email Already Exists")
            .jsonPath("$.message").isEqualTo("Email already exists: ${registrationDto.email}")

        coVerify { userService.register(registrationDto) }
    }

    @Test
    fun `getUserById should return 200 and user when user exists`() = runTest {
        val userId = "user-123"
        val responseDto = UserResponseDto(
            id = userId,
            email = "test@example.com",
            fullName = "Test User",
            createdAt = Instant.now()
        )

        coEvery { userService.getUserById(userId) } returns responseDto

        webTestClient.get()
            .uri("/api/users/$userId")
            .exchange()
            .expectStatus().isOk
            .expectBody(UserResponseDto::class.java)
            .isEqualTo(responseDto)

        coVerify { userService.getUserById(userId) }
    }

    @Test
    fun `getUserById should return 404 when user doesn't exist`() = runTest {
        val userId = "nonexistent-user"

        coEvery {
            userService.getUserById(userId)
        } throws UserNotFoundException(userId)

        webTestClient.get()
            .uri("/api/users/$userId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("User Not Found")
            .jsonPath("$.message").isEqualTo("User not found: $userId")

        coVerify { userService.getUserById(userId) }
    }

    @Test
    fun `getAllUsers should return 200 and all users`() = runTest {
        val users = listOf(
            User("1", "email1@mail.com", "password1", "user1"),
            User("2", "email2@mail.com", "password1", "user2")
        ).asFlow().map { it.toResponseDto() }

        coEvery { userService.getAllUsers() } returns users

        // Perform the request and verify
        webTestClient.get()
            .uri("/api/users")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(UserResponseDto::class.java)
            .hasSize(2)
            .contains(users.toList()[0])

        coVerify { userService.getAllUsers() }
    }
}
