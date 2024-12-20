package me.camwalford.finnhubingestionservice.client

import org.springframework.stereotype.Component
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.CompanyNews
import org.slf4j.LoggerFactory

@Component
class FinnHubClient(private val apiClient: DefaultApi) {

    private val logger = LoggerFactory.getLogger(FinnHubClient::class.java)

    fun getCompanyNewsList(company: String, from: String, to: String): List<CompanyNews> {
        logger.info("Fetching news for company: $company with from: $from  to: $to")
        try{
            val companyNewsList = apiClient.companyNews(company, from, to)
            return companyNewsList
        } catch (e: Exception) {
            logger.error("Error occurred during fetchCompanyNewsList", e)
            throw e
        }
    }
}

