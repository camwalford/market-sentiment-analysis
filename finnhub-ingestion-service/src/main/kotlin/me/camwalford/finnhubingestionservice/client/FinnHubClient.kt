package me.camwalford.finnhubingestionservice.client

import org.springframework.stereotype.Component
import io.finnhub.api.apis.DefaultApi
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import me.camwalford.finnhubingestionservice.util.ProtobufConversionUtil
import org.slf4j.LoggerFactory

@Component
class FinnHubClient(private val apiClient: DefaultApi) {

    private val logger = LoggerFactory.getLogger(FinnHubClient::class.java)

    fun getMarketNews(category: String, minId: Long?): MarketNewsList {
        logger.info("Fetching market news for category: $category with minId: $minId")

        val marketNewsData = apiClient.marketNews(category, minId)
        logger.debug("Raw market news data: {}", marketNewsData)

        return try {
            val protobufData = ProtobufConversionUtil.convertMarketNewsList(marketNewsData.toString())
            logger.info("Successfully converted market news data to Protobuf format")
            protobufData
        } catch (e: Exception) {
            logger.error("Failed to convert market news data to Protobuf", e)
            throw RuntimeException("Failed to convert JSON to Protobuf", e)
        }
    }
}

