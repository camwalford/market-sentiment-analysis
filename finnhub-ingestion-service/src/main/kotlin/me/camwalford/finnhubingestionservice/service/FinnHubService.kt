package me.camwalford.finnhubingestionservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.finnhub.api.models.CompanyNews
import me.camwalford.finnhubingestionservice.client.FinnHubClient
import me.camwalford.finnhubingestionservice.model.protobuf.CompanyNewsProto
import me.camwalford.finnhubingestionservice.util.ProtobufConversionUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FinnHubService(private val finnHubClient: FinnHubClient) {

    private val logger = LoggerFactory.getLogger(FinnHubService::class.java)
    private val objectMapper = ObjectMapper()

    fun fetchCompanyNewsList(company: String, from: String, to: String): List<CompanyNewsProto> {
        logger.info("Fetching market news from FinnHubClient for company: $company, from: $from, to: $to")
        val companyNewsList = finnHubClient.getCompanyNewsList(company, from, to)

        return try {
            // Convert JSON list to individual Protobuf CompanyNewsProto objects
            return companyNewsList.map { newsJson ->
                val jsonString = objectMapper.writeValueAsString(newsJson)
                ProtobufConversionUtil.convertCompanyNews(jsonString)
            }.also {
                logger.info("Successfully converted company news to individual Protobuf format")
            }
        } catch (e: Exception) {
            logger.error("Failed to convert company news data to Protobuf", e)
            throw RuntimeException("Failed to convert JSON to Protobuf", e)
        }
    }
}

