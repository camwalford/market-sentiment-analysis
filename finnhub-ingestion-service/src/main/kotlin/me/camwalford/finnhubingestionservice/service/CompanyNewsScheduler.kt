package me.camwalford.finnhubingestionservice.service


import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import org.springframework.retry.support.RetryTemplate

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

@Service
class CompanyNewsScheduler(
    private val finnHubService: FinnHubService,
    private val companyNewsProducer: CompanyNewsProducer,
    private val retryTemplate: RetryTemplate
) {
    private val logger = LoggerFactory.getLogger(CompanyNewsScheduler::class.java)

    @Scheduled(fixedRate = 60000)
    fun fetchAndSendCompanyNews() {
        retryTemplate.execute<Unit, Exception> {
            try {
                // Calculate the current date in UTC and one year earlier
                val currentDate = LocalDate.now(ZoneOffset.UTC)
                val fromDate = currentDate.minusYears(1)

                // Format the dates as strings
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val toDateStr = currentDate.format(dateFormatter)
                val fromDateStr = fromDate.format(dateFormatter)

                logger.info("Fetching company news from FinnHubService and sending to Kafka")
                val companyNewsList = finnHubService.fetchCompanyNewsList("AAPL", fromDateStr, toDateStr)
                logger.info("Fetched company news data: $companyNewsList")
                companyNewsProducer.sendCompanyNews(companyNewsList)
            } catch (e: Exception) {
                logger.error("Error occurred during fetchAndSendCompanyNews", e)
                throw e
            }
        }
    }
}
