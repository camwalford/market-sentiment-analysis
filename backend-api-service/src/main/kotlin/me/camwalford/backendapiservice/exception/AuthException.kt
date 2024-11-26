package me.camwalford.backendapiservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

// AuthException.kt
sealed class AuthException(
    status: HttpStatus,
    message: String,
    val errorCode: String,
    val timestamp: Instant = Instant.now(),
    val details: Map<String, Any>? = null
) : ResponseStatusException(status, message)

class InvalidCredentialsException(
    details: Map<String, Any>? = null
) : AuthException(
    HttpStatus.UNAUTHORIZED,
    "Invalid username or password",
    "AUTH_001",
    details = details
)

class TokenValidationException(
    message: String,
    details: Map<String, Any>? = null
) : AuthException(
    HttpStatus.UNAUTHORIZED,
    message,
    "AUTH_002",
    details = details
)

class TokenExpiredException(
    tokenType: String,
    details: Map<String, Any>? = null
) : AuthException(
    HttpStatus.UNAUTHORIZED,
    "$tokenType token has expired",
    "AUTH_003",
    details = details
)

class InvalidTokenException(
    message: String,
    details: Map<String, Any>? = null
) : AuthException(
    HttpStatus.UNAUTHORIZED,
    message,
    "AUTH_004",
    details = details
)

class RefreshTokenNotFoundException(
    details: Map<String, Any>? = null
) : AuthException(
    HttpStatus.UNAUTHORIZED,
    "Refresh token not found",
    "AUTH_005",
    details = details
)