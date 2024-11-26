package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.controller.sentiment.CompanyNews
import me.camwalford.backendapiservice.controller.sentiment.SentimentRequest
import me.camwalford.backendapiservice.exception.InvalidDateRangeException
import me.camwalford.backendapiservice.exception.InvalidTickerException
import me.camwalford.backendapiservice.exception.NewsServiceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

@Service
class CompanyNewsService(
    private val webClient: WebClient,
    @Value("\${finnhub.ingestion.service.url}") private val companyNewsServiceUrl: String
) {
    private val logger = LoggerFactory.getLogger(CompanyNewsService::class.java)

    fun fetchCompanyNews(request: SentimentRequest): List<CompanyNews> {
        logger.info("Fetching news for ticker: ${request.ticker}")

        validateRequest(request)

        try {
            return webClient.post()
                .uri("$companyNewsServiceUrl/finnhub/company-news/list")
                .bodyValue(request)
                .retrieve()
                .onStatus({ status -> status.is5xxServerError }) { response ->
                    throw NewsServiceException(
                        "News service unavailable",
                        mapOf(
                            "statusCode" to response.statusCode().value(),
                            "ticker" to request.ticker,
                            "fromDate" to request.fromDate,
                            "toDate" to request.toDate
                        )
                    )
                }
                .bodyToMono<List<CompanyNews>>()
                .block() ?: throw NewsServiceException(
                "No response from news service",
                mapOf(
                    "ticker" to request.ticker,
                    "fromDate" to request.fromDate,
                    "toDate" to request.toDate
                )
            )
        } catch (ex: Exception) {
            when (ex) {
                is NewsServiceException -> throw ex
                else -> throw NewsServiceException(
                    "Error fetching company news: ${ex.message}",
                    mapOf(
                        "error" to (ex.message ?: "Unknown error"),
                        "ticker" to request.ticker
                    )
                )
            }
        }
    }

    private fun validateRequest(request: SentimentRequest) {
        // Validate ticker
        logger.debug("Validating ticker: ${request.ticker}")
        request.ticker = request.ticker.trim().uppercase()
        if (request.ticker.isBlank() || !request.ticker.matches(Regex("^[A-Z]+\$"))) {
            throw InvalidTickerException(
                request.ticker,
                mapOf("providedTicker" to request.ticker)
            )
        }

        // Validate date-times
        try {
            val fromDateTime = OffsetDateTime.parse(request.fromDate)
            val toDateTime = OffsetDateTime.parse(request.toDate)
            logger.debug("Validating date-time range: $fromDateTime to $toDateTime")

            if (toDateTime.isBefore(fromDateTime)) {
                throw InvalidDateRangeException(
                    "End date cannot be before start date",
                    mapOf("fromDate" to request.fromDate, "toDate" to request.toDate)
                )
            }

            val daysBetween = ChronoUnit.DAYS.between(fromDateTime, toDateTime)
            if (daysBetween > 365) {
                throw InvalidDateRangeException(
                    "Date range cannot exceed 365 days",
                    mapOf(
                        "fromDate" to request.fromDate,
                        "toDate" to request.toDate,
                        "requestedDays" to daysBetween
                    )
                )
            }
        } catch (e: DateTimeParseException) {
            throw InvalidDateRangeException(
                "Invalid date format. Use ISO 8601 date-time format, e.g., YYYY-MM-DDTHH:MM:SSZ",
                mapOf("fromDate" to request.fromDate, "toDate" to request.toDate)
            )
        }
    }
}
