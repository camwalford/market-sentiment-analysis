package me.camwalford.finnhubingestionservice.service


import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import org.springframework.retry.support.RetryTemplate

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

@Service
class CompanyNewsFetcher(
    private val finnHubService: FinnHubService,
    private val companyNewsProducer: CompanyNewsProducer,
    private val retryTemplate: RetryTemplate
) {
    private val logger = LoggerFactory.getLogger(CompanyNewsFetcher::class.java)

    fun fetchAndSendCompanyNews(company: String) {
        retryTemplate.execute<Unit, Exception> {
            try {
                // Calculate the current date in UTC and one year earlier
                val currentDate = LocalDate.now(ZoneOffset.UTC)
                val fromDate = currentDate.minusYears(1)

                // Format the dates as strings
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val toDateStr = currentDate.format(dateFormatter)
                val fromDateStr = fromDate.format(dateFormatter)

                logger.info("Fetching company news from FinnHubService and sending each item to Kafka")
                val companyNewsList = finnHubService.fetchCompanyNewsList("AAPL", fromDateStr, toDateStr)

                // Send each company news item individually
                companyNewsList.forEach { newsItem ->
                    logger.info("Sending individual company news item: $newsItem")
                    companyNewsProducer.sendCompanyNews(newsItem, company)
                }
            } catch (e: Exception) {
                logger.error("Error occurred during fetchAndSendCompanyNews", e)
                throw e
            }
        }
    }
}
