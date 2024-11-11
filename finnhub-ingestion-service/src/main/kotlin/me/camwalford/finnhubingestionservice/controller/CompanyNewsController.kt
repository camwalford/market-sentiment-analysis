package me.camwalford.finnhubingestionservice.controller


import io.finnhub.api.models.CompanyNews
import me.camwalford.finnhubingestionservice.exception.DateRangeExceededException
import me.camwalford.finnhubingestionservice.exception.InvalidTickerFormatException
import me.camwalford.finnhubingestionservice.exception.TickerNotFoundException
import me.camwalford.finnhubingestionservice.model.CompanyNewsRequest
import me.camwalford.finnhubingestionservice.service.FinnHubService
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.slf4j.LoggerFactory

import java.time.ZonedDateTime
import java.time.format.DateTimeParseException



@RestController
@RequestMapping("/finnhub/company-news")
class CompanyNewsController(
    private val finnHubService: FinnHubService,
) {
    private val logger = LoggerFactory.getLogger(CompanyNewsController::class.java)

    @PostMapping("/list")
    fun fetchCompanyNewsList(@RequestBody request: CompanyNewsRequest): List<CompanyNews> {
        logger.info("Received request to fetch company news for ticker ${request.ticker} from ${request.fromDate} to ${request.toDate}")

        // Validate date range and format the dates
        val (formattedFromDate, formattedToDate) = validateAndFormatDateRange(request.fromDate, request.toDate)

        // Validate ticker format
        validateTickerFormat(request.ticker)

        // Fetch and process news
        val newsList = finnHubService.fetchCompanyNewsList(request.ticker, formattedFromDate, formattedToDate)

        if (newsList.isEmpty()) {
            throw TickerNotFoundException("No news found for ticker ${request.ticker}")
        }

        return newsList
    }

    private fun validateAndFormatDateRange(from: String, to: String): Pair<String, String> {
        try {
            // Parse ISO date strings
            val fromDate = ZonedDateTime.parse(from)
            val toDate = ZonedDateTime.parse(to)
            logger.info("Validating date range from $fromDate to $toDate")

            // Check if the date range exceeds one year
            val yearsBetween = ChronoUnit.YEARS.between(fromDate, toDate)
            if (yearsBetween >= 1) {
                throw DateRangeExceededException("Date range must be within one year.")
            }

            // Format the dates to yyyy-MM-dd
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formattedFromDate = fromDate.toLocalDate().format(formatter)
            val formattedToDate = toDate.toLocalDate().format(formatter)

            return Pair(formattedFromDate, formattedToDate)
        } catch (e: DateTimeParseException) {
            logger.error("Invalid date format for fromDate: $from or toDate: $to")
            throw DateRangeExceededException("Invalid date format. Please use ISO format, e.g., 2024-11-05T09:03:17.074Z.")
        }
    }

    private fun validateTickerFormat(ticker: String) {
        logger.info("Validating ticker format: $ticker")
        if (!ticker.matches(Regex("^[A-Z]{3,4}$"))) {
            throw InvalidTickerFormatException("Ticker must be a 3-4 letter uppercase string.")
        }
    }
}

