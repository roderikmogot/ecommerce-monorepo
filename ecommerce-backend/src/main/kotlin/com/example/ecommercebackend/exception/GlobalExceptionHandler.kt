package com.example.ecommercebackend.exception

import com.example.ecommercebackend.dto.ErrorResponse
import com.example.ecommercebackend.exception.general.*
import io.github.oshai.kotlinlogging.KLogger
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class GlobalExceptionHandler (var logger: KLogger) {

    /**
     * Handle AppException and its subclasses with the specific HTTP status defined in each exception
     */
    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = ex.httpStatus.value(),
            error = ex.httpStatus.reasonPhrase,
            message = ex.message,
            path = request.uri.toString()
        )

        // Log at appropriate level based on severity
        when (ex.httpStatus.value()) {
            in 400..499 -> logger.warn { "Client error: ${ex.message} at ${request.uri}" }
            else -> logger.error(ex) { "Server error: ${ex.message} at ${request.uri}" }
        }

        return ResponseEntity(error, ex.httpStatus)
    }

    /**
     * Handle validation errors from request body or parameters
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult
            .fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            message = errors,
            path = request.uri.toString()
        )

        logger.warn { "Validation error: $errors at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }

    /**
     * Handle OptimisticLockingFailureException for concurrent modifications
     */
    @ExceptionHandler(OptimisticLockingFailureException::class)
    fun handleOptimisticLocking(ex: OptimisticLockingFailureException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Concurrent Modification",
            message = "High demand for this item. Please try again.",
            path = request.uri.toString()
        )

        logger.warn(ex) { "Optimistic locking failure at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }

    /**
     * Handle specific product exceptions
     */
    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStock(ex: InsufficientStockException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Insufficient Stock",
            message = ex.message ?: "Insufficient stock for requested items",
            path = request.uri.toString()
        )

        logger.warn { "Insufficient stock error: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFound(ex: ProductNotFoundException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Product Not Found",
            message = ex.message ?: "The requested product was not found",
            path = request.uri.toString()
        )

        logger.warn { "Product not found: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    /**
     * Handle user-related exceptions
     */
    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExistException(ex: EmailAlreadyExistsException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Email Already Exists",
            message = ex.message ?: "This email address is already in use",
            path = request.uri.toString()
        )

        logger.warn { "Email already exists: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }

    /**
     * Handle product-related exceptions
     */
    @ExceptionHandler(ProductAlreadyExistsException::class)
    fun handleProductAlreadyExistsException(ex: ProductAlreadyExistsException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Product Already Exists",
            message = ex.message ?: "A product with this name already exists",
            path = request.uri.toString()
        )

        logger.warn { "Product already exists: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "User Not Found",
            message = ex.message ?: "The requested user was not found",
            path = request.uri.toString()
        )

        logger.warn { "User not found: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Error",
            message = ex.message ?: "Validation error occurred",
            path = request.uri.toString()
        )

        logger.warn { "Validation exception: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }

    /**
     * Handle IllegalArgumentException for bad requests
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid input",
            path = request.uri.toString()
        )

        logger.warn { "Bad request: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }

    /**
     * Handle NoSuchElementException for not found resources
     */
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Not Found",
            message = ex.message ?: "Resource not found",
            path = request.uri.toString()
        )

        logger.warn { "Resource not found: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }

    /**
     * Handle WebExchangeBindException for violation of the rules
     */
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidation(ex: WebExchangeBindException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val errorMessages = ex.bindingResult.fieldErrors.map {
            "${it.field}: ${it.defaultMessage}"
        }.joinToString("; ")

        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation violated",
            message = errorMessages,
            path = request.uri.toString()
        )

        logger.warn { "Validation violated: ${ex.message} at ${request.uri}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }



    /**
     * Fallback handler for all other exceptions
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, request: ServerHttpRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "An unexpected error occurred. Please try again later.",
            path = request.uri.toString()
        )

        // Log the full stack trace for unexpected errors
        logger.error(ex) { "Unhandled exception at ${request.uri}: ${ex.message}" }
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }
}