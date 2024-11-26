package me.camwalford.backendapiservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

// RequestException.kt
sealed class RequestException(
    status: HttpStatus,
    message: String,
    val errorCode: String,
    val timestamp: Instant = Instant.now(),
    val details: Map<String, Any>? = null
) : ResponseStatusException(status, message)

class RequestTrackingException(
    message: String,
    details: Map<String, Any>? = null
) : RequestException(
    HttpStatus.INTERNAL_SERVER_ERROR,
    message,
    "REQUEST_001",
    details = details
)

class RequestStatsNotFoundException(
    identifier: Any,
    details: Map<String, Any>? = null
) : RequestException(
    HttpStatus.NOT_FOUND,
    "Request statistics not found for: $identifier",
    "REQUEST_002",
    details = details
)

class InvalidRequestStatsException(
    message: String,
    details: Map<String, Any>? = null
) : RequestException(
    HttpStatus.BAD_REQUEST,
    message,
    "REQUEST_003",
    details = details
)