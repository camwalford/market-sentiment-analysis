package me.camwalford.finnhubingestionservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import me.camwalford.finnhubingestionservice.client.FinnHubClient
import me.camwalford.finnhubingestionservice.model.protobuf.CompanyNewsList
import me.camwalford.finnhubingestionservice.util.ProtobufConversionUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FinnHubService(private val finnHubClient: FinnHubClient) {

    private val logger = LoggerFactory.getLogger(FinnHubService::class.java)
    private val objectMapper = ObjectMapper()

    fun fetchCompanyNewsList(company: String, from: String, to: String): CompanyNewsList {
        logger.info("Fetching market news from FinnHubClient for category: $company, from: $from, to: $to")
        val companyNewsList = finnHubClient.getCompanyNewsList(company, from, to)

        return try {
            val jsonString = objectMapper.writeValueAsString(companyNewsList)
            val protobufData = ProtobufConversionUtil.convertCompanyNewsList(jsonString)
            logger.info("Successfully converted company news to Protobuf format")
            return protobufData
        } catch (e: Exception) {
            logger.error("Failed to convert company news data to Protobuf", e)
            throw RuntimeException("Failed to convert JSON to Protobuf", e)
        }

    }
}
