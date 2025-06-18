// UserServiceTest.kt
package com.example.ecommercebackend.service

import com.example.ecommercebackend.dto.UserRegistrationDto
import com.example.ecommercebackend.dto.UserUpdateDto
import com.example.ecommercebackend.exception.general.EmailAlreadyExistsException
import com.example.ecommercebackend.exception.general.UserNotFoundException
import com.example.ecommercebackend.model.User
import com.example.ecommercebackend.repository.UserRepository
import io.github.oshai.kotlinlogging.KLogger
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant

class UserServiceTest {

 private val userRepository = mockk<UserRepository>()
 private val passwordEncoder = mockk<PasswordEncoder>()
 private val logger = mockk<KLogger>(relaxed = true)
 private lateinit var userService: UserService

 @BeforeEach
 fun setup() {
  userService = UserService(userRepository, passwordEncoder, logger)
 }

 @Test
 fun `register should create a new user when email doesn't exist`() = runBlocking {
  val registrationDto = UserRegistrationDto(
   email = "test@example.com",
   password = "password123",
   fullName = "Test User"
  )
  val hashedPassword = "hashedPassword"
  val userId = "user-123"
  val createdAt = Instant.now()

  coEvery { userRepository.existsByEmail(registrationDto.email) } returns false
  coEvery { passwordEncoder.encode(registrationDto.password) } returns hashedPassword
  coEvery {
   userRepository.save(match {
    it.email == registrationDto.email &&
            it.passwordHash == hashedPassword &&
            it.fullName == registrationDto.fullName
   })
  } returns User(
   id = userId,
   email = registrationDto.email,
   passwordHash = hashedPassword,
   fullName = registrationDto.fullName,
   createdAt = createdAt
  )

  val result = userService.register(registrationDto)

  assertEquals(userId, result.id)
  assertEquals(registrationDto.email, result.email)
  assertEquals(registrationDto.fullName, result.fullName)
  coVerify { userRepository.existsByEmail(registrationDto.email) }
  coVerify { passwordEncoder.encode(registrationDto.password) }
  coVerify { userRepository.save(any()) }
 }

 @Test
 fun `register should throw EmailAlreadyExistsException when email exists`() = runBlocking {
  val registrationDto = UserRegistrationDto(
   email = "existing@example.com",
   password = "password123",
   fullName = "Test User"
  )

  coEvery { userRepository.existsByEmail(registrationDto.email) } returns true

  assertThrows<EmailAlreadyExistsException> {
   userService.register(registrationDto)
  }

  coVerify { userRepository.existsByEmail(registrationDto.email) }
  coVerify(exactly = 0) { userRepository.save(any()) }
 }

 @Test
 fun `getUserById should return user when exists`() = runBlocking {
  val userId = "user-123"
  val user = User(
   id = userId,
   email = "test@example.com",
   passwordHash = "hashedPassword",
   fullName = "Test User",
   createdAt = Instant.now()
  )

  coEvery { userRepository.findById(userId) } returns user

  val result = userService.getUserById(userId)

  assertEquals(userId, result.id)
  assertEquals(user.email, result.email)
  assertEquals(user.fullName, result.fullName)
  coVerify { userRepository.findById(userId) }
 }

 @Test
 fun `getUserById should throw UserNotFoundException when user doesn't exist`() = runBlocking {
  val userId = "nonexistent-user"

  coEvery { userRepository.findById(userId) } returns null

  assertThrows<UserNotFoundException> {
   userService.getUserById(userId)
  }

  coVerify { userRepository.findById(userId) }
 }

 @Test
 fun `updateUser should update and return user when data is valid`() = runBlocking {
  val userId = "user-123"
  val existingUser = User(
   id = userId,
   email = "old@example.com",
   passwordHash = "hashedPassword",
   fullName = "Old Name",
   createdAt = Instant.now()
  )

  val updateDto = UserUpdateDto(
   email = "new@example.com",
   fullName = "New Name"
  )

  val updatedUser = existingUser.copy(
   email = updateDto.email!!,
   fullName = updateDto.fullName!!
  )

  coEvery { userRepository.findById(userId) } returns existingUser
  coEvery { userRepository.existsByEmail(updateDto.email!!) } returns false
  coEvery { userRepository.save(any()) } returns updatedUser

  val result = userService.updateUser(userId, updateDto)

  assertEquals(userId, result.id)
  assertEquals(updateDto.email, result.email)
  assertEquals(updateDto.fullName, result.fullName)
  coVerify { userRepository.findById(userId) }
  coVerify { userRepository.existsByEmail(updateDto.email!!) }
  coVerify { userRepository.save(any()) }
 }

 @Test
 fun `updateUser should throw EmailAlreadyExistsException when new email already exists`() = runBlocking {
  val userId = "user-123"
  val existingUser = User(
   id = userId,
   email = "old@example.com",
   passwordHash = "hashedPassword",
   fullName = "Old Name",
   createdAt = Instant.now()
  )

  val updateDto = UserUpdateDto(
   email = "existing@example.com",
   fullName = "New Name"
  )

  coEvery { userRepository.findById(userId) } returns existingUser
  coEvery { userRepository.existsByEmail(updateDto.email!!) } returns true

  assertThrows<EmailAlreadyExistsException> {
   userService.updateUser(userId, updateDto)
  }

  coVerify { userRepository.findById(userId) }
  coVerify { userRepository.existsByEmail(updateDto.email!!) }
  coVerify(exactly = 0) { userRepository.save(any()) }
 }

 @Test
 fun `deleteUser should delete user when exists`() = runBlocking {
  val userId = "user-123"

  coEvery { userRepository.existsById(userId) } returns true
  coEvery { userRepository.deleteById(userId) } just runs

  userService.deleteUser(userId)

  coVerify { userRepository.existsById(userId) }
  coVerify { userRepository.deleteById(userId) }
 }

 @Test
 fun `deleteUser should throw UserNotFoundException when user doesn't exist`() = runBlocking {
  val userId = "nonexistent-user"

  coEvery { userRepository.existsById(userId) } returns false

  assertThrows<UserNotFoundException> {
   userService.deleteUser(userId)
  }

  coVerify { userRepository.existsById(userId) }
  coVerify(exactly = 0) { userRepository.deleteById(any()) }
 }

 @Test
 fun `getAllUsers should return all users`() = runBlocking {
  val users = listOf(
   User(
    id = "user-1",
    email = "user1@example.com",
    passwordHash = "hash1",
    fullName = "User One",
    createdAt = Instant.now()
   ),
   User(
    id = "user-2",
    email = "user2@example.com",
    passwordHash = "hash2",
    fullName = "User Two",
    createdAt = Instant.now()
   )
  )

  every { userRepository.findAll() } returns flowOf(*users.toTypedArray())

  val result = userService.getAllUsers()

  val resultList = result.toList()
  assertEquals(2, resultList.size)
  assertEquals(users[0].id, resultList[0].id)
  assertEquals(users[1].id, resultList[1].id)
  verify { userRepository.findAll() }
 }

 @Test
 fun `searchUsersByName should return matching users`() = runBlocking {
  val searchName = "test"
  val users = listOf(
   User(
    id = "user-1",
    email = "test1@example.com",
    passwordHash = "hash1",
    fullName = "Test User",
    createdAt = Instant.now()
   ),
   User(
    id = "user-2",
    email = "test2@example.com",
    passwordHash = "hash2",
    fullName = "Another Test User",
    createdAt = Instant.now()
   )
  )

  every { userRepository.findByFullNameContainingIgnoreCase(searchName) } returns flowOf(*users.toTypedArray())

  val result = userService.searchUsersByName(searchName)

  val resultList = result.toList()
  assertEquals(2, resultList.size)
  assertEquals(users[0].id, resultList[0].id)
  assertEquals(users[1].id, resultList[1].id)
  verify { userRepository.findByFullNameContainingIgnoreCase(searchName) }
 }
}