package me.camwalford.finnhubingestionservice.service

import me.camwalford.finnhubingestionservice.client.FinnHubClient
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import me.camwalford.finnhubingestionservice.util.ProtobufConversionUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FinnHubService(private val finnHubClient: FinnHubClient) {

    private val logger = LoggerFactory.getLogger(FinnHubService::class.java)

    fun fetchMarketNewsList(category: String = "general", minId: Long? = 0L): MarketNewsList {
        logger.info("Fetching market news from FinnHubClient for category: $category, minId: $minId")
        val marketNewsJson = finnHubClient.getMarketNewsList(category, minId)

        return try {
            val protobufData = ProtobufConversionUtil.convertMarketNewsList(marketNewsJson.toString())
            logger.info("Successfully converted market news data to Protobuf format")
            protobufData
        } catch (e: Exception) {
            logger.error("Failed to convert market news data to Protobuf", e)
            throw RuntimeException("Failed to convert JSON to Protobuf", e)
        }
    }
}
