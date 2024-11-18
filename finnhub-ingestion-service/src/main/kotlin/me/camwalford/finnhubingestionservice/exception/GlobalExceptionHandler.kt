package me.camwalford.finnhubingestionservice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

// Custom Exception for Not Found Errors
class ResourceNotFoundException(message: String) : RuntimeException(message)

// Exception for date range validation
class DateRangeExceededException(message: String) : RuntimeException(message)

// Exception for ticker format validation
class InvalidTickerFormatException(message: String) : RuntimeException(message)

// Exception for when ticker is not found
class TickerNotFoundException(message: String) : RuntimeException(message)

// Define Global Exception Handler
@ControllerAdvice
class GlobalExceptionHandler {

    // Handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("NOT_FOUND", ex.message ?: "Resource not found")
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    // Handle DateRangeExceededException
    @ExceptionHandler(DateRangeExceededException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleDateRangeExceeded(ex: DateRangeExceededException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("INVALID_DATE_RANGE", ex.message ?: "Date range exceeds allowed limit")
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    // Handle InvalidTickerFormatException
    @ExceptionHandler(InvalidTickerFormatException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidTickerFormat(ex: InvalidTickerFormatException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("INVALID_TICKER_FORMAT", ex.message ?: "Ticker format is invalid")
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    // Handle TickerNotFoundException
    @ExceptionHandler(TickerNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleTickerNotFound(ex: TickerNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("TICKER_NOT_FOUND", ex.message ?: "Ticker not found")
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    // Handle Generic Exceptions
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("INTERNAL_SERVER_ERROR", ex.message ?: "An unexpected error occurred")
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

// Error Response DTO
data class ErrorResponse(
    val errorCode: String,
    val errorMessage: String
)
