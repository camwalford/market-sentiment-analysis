package me.camwalford.finnhubingestionservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.finnhub.api.models.CompanyNews
import me.camwalford.finnhubingestionservice.client.FinnHubClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FinnHubService(private val finnHubClient: FinnHubClient) {

    private val logger = LoggerFactory.getLogger(FinnHubService::class.java)

    fun fetchCompanyNewsList(company: String, from: String, to: String): List<CompanyNews> {
        logger.info("Fetching market news from FinnHubClient for company: $company, from: $from, to: $to")
        val companyNewsList = finnHubClient.getCompanyNewsList(company, from, to)
        logger.debug("Raw company news data: {}", ObjectMapper().writeValueAsString(companyNewsList))
        return companyNewsList
    }
}

