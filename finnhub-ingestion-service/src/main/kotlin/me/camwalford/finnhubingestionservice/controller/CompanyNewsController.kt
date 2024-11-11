package me.camwalford.finnhubingestionservice.controller


import io.finnhub.api.models.CompanyNews
import me.camwalford.finnhubingestionservice.exception.DateRangeExceededException
import me.camwalford.finnhubingestionservice.exception.InvalidTickerFormatException
import me.camwalford.finnhubingestionservice.exception.TickerNotFoundException
import me.camwalford.finnhubingestionservice.model.CompanyNewsRequest
import me.camwalford.finnhubingestionservice.service.FinnHubService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("/finnhub/company-news")
class CompanyNewsController(
    private val finnHubService: FinnHubService
) {
    @PostMapping("/list")
    fun fetchCompanyNewsList(@RequestBody request: CompanyNewsRequest): List<CompanyNews> {

        // Validate date range
        validateDateRange(request.fromDate, request.toDate)

        // Validate ticker format
        validateTickerFormat(request.ticker)

        // Fetch and process news
        val newsList = finnHubService.fetchCompanyNewsList(request.ticker, request.fromDate, request.toDate)

        if (newsList.isEmpty()) {
            throw TickerNotFoundException("No news found for ticker ${request.ticker}")
        }

        // Map newsList to response DTO
        return newsList
    }


    private fun validateDateRange(from: String, to: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fromDate = LocalDate.parse(from, formatter)
        val toDate = LocalDate.parse(to, formatter)

        val yearsBetween = ChronoUnit.YEARS.between(fromDate, toDate)
        if (yearsBetween >= 1) {
            throw DateRangeExceededException("Date range must be within one year.")
        }
    }

    private fun validateTickerFormat(ticker: String) {
        if (!ticker.matches(Regex("^[A-Z]{3,4}$"))) {
            throw InvalidTickerFormatException("Ticker must be a 3-4 letter uppercase string.")
        }
    }
}
