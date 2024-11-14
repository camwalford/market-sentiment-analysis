package me.camwalford.backendapiservice.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

// ErrorResponse.kt
data class ErrorResponse(
    val timestamp: Long = System.currentTimeMillis(),
    val status: Int,
    val error: String,
    val message: String,
    val errorCode: String? = null,
    val details: Map<String, Any>? = null
)

// GlobalExceptionHandler.kt
@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = ex.statusCode.value(),
            error = ex.statusCode.toString(),
            message = ex.reason ?: "An error occurred",
        )
        return ResponseEntity(errorResponse, ex.statusCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error: ", ex)
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.toString(),
            message = "An unexpected error occurred",
            details = mapOf("error" to (ex.message ?: "Unknown error"))
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

// CustomException.kt
class InsufficientCreditsException(message: String) :
    ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, message)

class NewsApiException(message: String) :
    ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, message)

class SentimentAnalysisException(message: String) :
    ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, message)