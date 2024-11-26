package me.camwalford.backendapiservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

sealed class UserException(
    status: HttpStatus,
    message: String,
    val errorCode: String,
    val timestamp: Instant = Instant.now(),
    val details: Map<String, Any>? = null
) : ResponseStatusException(status, message)

class UserNotFoundException(
    identifier: Any,
    details: Map<String, Any>? = null
) : UserException(
    HttpStatus.NOT_FOUND,
    "User not found with identifier: $identifier",
    "USER_001",
    details = details
)

class DuplicateUserException(
    field: String,
    value: String,
    details: Map<String, Any>? = null
) : UserException(
    HttpStatus.CONFLICT,
    "User already exists with $field: $value",
    "USER_002",
    details = details
)

class InsufficientCreditsException(
    current: Int,
    required: Int,
    details: Map<String, Any>? = mapOf(
        "currentCredits" to current,
        "requiredCredits" to required
    )
) : UserException(
    HttpStatus.PAYMENT_REQUIRED,
    "Insufficient credits: has $current, needs $required",
    "USER_003",
    details = details
)

class UserBannedException(
    username: String,
    reason: String? = null,
    details: Map<String, Any>? = mapOf(
        "username" to username,
        "reason" to (reason ?: "No reason provided")
    )
) : UserException(
    HttpStatus.FORBIDDEN,
    "User $username is banned${reason?.let { ": $it" } ?: ""}",
    "USER_004",
    details = details
)

class InvalidUserOperationException(
    operation: String,
    reason: String,
    details: Map<String, Any>? = null
) : UserException(
    HttpStatus.BAD_REQUEST,
    "Invalid operation '$operation': $reason",
    "USER_005",
    details = details
)

class UserAuthenticationException(
    reason: String,
    details: Map<String, Any>? = null
) : UserException(
    HttpStatus.UNAUTHORIZED,
    "Authentication failed: $reason",
    "USER_006",
    details = details
)
