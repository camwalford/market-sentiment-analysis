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

    fun fetchCompanyNewsList(company: String, from: String, to: String): List<CompanyNews> {
        logger.info("Fetching market news from FinnHubClient for company: $company, from: $from, to: $to")
        val companyNewsList = finnHubClient.getCompanyNewsList(company, from, to)
        return companyNewsList

//        return try {
//
//        } catch (e: Exception) {
//            logger.error("Failed to get company news list", e)
//            throw RuntimeException("Failed to get company news list", e)
//        }
    }
}

