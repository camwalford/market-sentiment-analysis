package me.camwalford.backendapiservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

// ExternalServiceException.kt
sealed class ExternalServiceException(
    status: HttpStatus,
    message: String,
    val errorCode: String,
    val timestamp: Instant = Instant.now(),
    val details: Map<String, Any>? = null
) : ResponseStatusException(status, message)

class SentimentAnalysisException(
    message: String,
    details: Map<String, Any>? = null
) : ExternalServiceException(
    HttpStatus.SERVICE_UNAVAILABLE,
    message,
    "SENTIMENT_001",
    details = details
)

class NewsServiceException(
    message: String,
    details: Map<String, Any>? = null
) : ExternalServiceException(
    HttpStatus.SERVICE_UNAVAILABLE,
    message,
    "NEWS_001",
    details = details
)

class InvalidDateRangeException(
    message: String,
    details: Map<String, Any>? = null
) : ExternalServiceException(
    HttpStatus.BAD_REQUEST,
    message,
    "DATE_001",
    details = details
)

class InvalidTickerException(
    ticker: String,
    details: Map<String, Any>? = null
) : ExternalServiceException(
    HttpStatus.BAD_REQUEST,
    "Invalid ticker symbol: $ticker",
    "TICKER_001",
    details = details
)