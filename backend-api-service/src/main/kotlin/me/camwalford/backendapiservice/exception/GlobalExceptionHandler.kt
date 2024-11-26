package me.camwalford.backendapiservice.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.error("Validation exception occurred", ex)

        val fieldErrors = ex.bindingResult.fieldErrors.associate { fieldError: FieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "Invalid value")
        }

        val problemDetail = ProblemDetail
            .forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed")
            .apply {
                title = "Validation Error"
                type = URI.create("https://api.yourservice.com/errors/VALIDATION_001")
                instance = URI.create(request.contextPath)
                setProperty("timestamp", Instant.now().toString())
                setProperty("errorCode", "VALIDATION_001")
                setProperty("fields", fieldErrors)
            }

        return ResponseEntity(problemDetail, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(RequestException::class)
    fun handleRequestException(
        ex: RequestException,
        request: HttpServletRequest
    ): ProblemDetail {
        logger.error("Request exception occurred: ${ex.message}", ex)

        return ProblemDetail
            .forStatusAndDetail(ex.statusCode, ex.message ?: "Request operation failed")
            .apply {
                title = when (ex) {
                    is RequestTrackingException -> "Request Tracking Error"
                    is RequestStatsNotFoundException -> "Request Statistics Not Found"
                    is InvalidRequestStatsException -> "Invalid Request Statistics"
                }
                type = URI.create("https://api.yourservice.com/errors/${ex.errorCode}")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", ex.timestamp.toString())
                setProperty("errorCode", ex.errorCode)

                ex.details?.forEach { (key, value) ->
                    setProperty(key, value)
                }
            }
    }

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(
        ex: AuthException,
        request: HttpServletRequest
    ): ProblemDetail {
        logger.error("Authentication exception occurred: ${ex.message}", ex)

        return ProblemDetail
            .forStatusAndDetail(ex.statusCode, ex.message ?: "Authentication failed")
            .apply {
                title = when (ex) {
                    is InvalidCredentialsException -> "Invalid Credentials"
                    is TokenExpiredException -> "Token Expired"
                    is TokenValidationException -> "Token Validation Failed"
                    is InvalidTokenException -> "Invalid Token"
                    is RefreshTokenNotFoundException -> "Refresh Token Not Found"
                }
                type = URI.create("https://api.yourservice.com/errors/${ex.errorCode}")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", ex.timestamp.toString())
                setProperty("errorCode", ex.errorCode)

                ex.details?.forEach { (key, value) ->
                    setProperty(key, value)
                }
            }
    }

    @ExceptionHandler(UserException::class)
    fun handleUserException(
        ex: UserException,
        request: HttpServletRequest
    ): ProblemDetail {
        logger.error("User exception occurred: ${ex.message}", ex)

        return ProblemDetail
            .forStatusAndDetail(ex.statusCode, ex.message ?: "User operation failed")
            .apply {
                title = when (ex) {
                    is UserNotFoundException -> "User Not Found"
                    is DuplicateUserException -> "Duplicate User"
                    is InsufficientCreditsException -> "Insufficient Credits"
                    is UserBannedException -> "User Banned"
                    is InvalidUserOperationException -> "Invalid Operation"
                    is UserAuthenticationException -> "Authentication Failed"
                }
                type = URI.create("https://api.yourservice.com/errors/${ex.errorCode}")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", ex.timestamp.toString())
                setProperty("errorCode", ex.errorCode)

                // Add any additional details if they exist
                ex.details?.forEach { (key, value) ->
                    setProperty(key, value)
                }
            }
    }

    @ExceptionHandler(ExternalServiceException::class)
    fun handleExternalServiceException(
        ex: ExternalServiceException,
        request: HttpServletRequest
    ): ProblemDetail {
        logger.error("External service exception occurred: ${ex.message}", ex)

        return ProblemDetail
            .forStatusAndDetail(ex.statusCode, ex.message ?: "External service error")
            .apply {
                title = when (ex) {
                    is SentimentAnalysisException -> "Sentiment Analysis Service Error"
                    is NewsServiceException -> "News Service Error"
                    is InvalidDateRangeException -> "Invalid Date Range"
                    is InvalidTickerException -> "Invalid Ticker"
                }
                type = URI.create("https://api.yourservice.com/errors/${ex.errorCode}")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", ex.timestamp.toString())
                setProperty("errorCode", ex.errorCode)

                ex.details?.forEach { (key, value) ->
                    setProperty(key, value)
                }
            }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ProblemDetail {
        logger.error("Type mismatch exception occurred", ex)

        return ProblemDetail
            .forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter '${ex.name}': ${ex.value}"
            )
            .apply {
                title = "Invalid Parameter"
                type = URI.create("https://api.yourservice.com/errors/PARAM_001")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", Instant.now().toString())
                setProperty("errorCode", "PARAM_001")
                setProperty("parameter", ex.name)
                setProperty("invalidValue", ex.value.toString())
                setProperty("requiredType", ex.requiredType?.simpleName ?: "unknown")
            }
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(
        ex: AccessDeniedException,
        request: HttpServletRequest
    ): ProblemDetail {
        logger.error("Access denied exception occurred", ex)

        return ProblemDetail
            .forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied: ${ex.message}")
            .apply {
                title = "Access Denied"
                type = URI.create("https://api.yourservice.com/errors/AUTH_001")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", Instant.now().toString())
                setProperty("errorCode", "AUTH_001")
            }
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(
        ex: AuthenticationException,
        request: HttpServletRequest
    ): ProblemDetail {
        logger.error("Authentication exception occurred", ex)

        return ProblemDetail
            .forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Authentication failed: ${ex.message}")
            .apply {
                title = "Authentication Failed"
                type = URI.create("https://api.yourservice.com/errors/AUTH_002")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", Instant.now().toString())
                setProperty("errorCode", "AUTH_002")
            }
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ProblemDetail {
        logger.error("Unexpected exception occurred", ex)

        return ProblemDetail
            .forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
            )
            .apply {
                title = "Internal Server Error"
                type = URI.create("https://api.yourservice.com/errors/SERVER_001")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", Instant.now().toString())
                setProperty("errorCode", "SERVER_001")
                setProperty("message", ex.message ?: "Unknown error")

                // Only include stack trace in non-production environments
                if (!isProdEnvironment()) {
                    setProperty("stackTrace", ex.stackTraceToString())
                }
            }
    }

    private fun isProdEnvironment(): Boolean {
        // Implement your environment check logic here
        return System.getenv("SPRING_PROFILES_ACTIVE") == "prod"
    }
}