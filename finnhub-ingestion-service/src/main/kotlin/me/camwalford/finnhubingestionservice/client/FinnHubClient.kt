package me.camwalford.finnhubingestionservice.client

import org.springframework.stereotype.Component
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.MarketNews
import org.slf4j.LoggerFactory

@Component
class FinnHubClient(private val apiClient: DefaultApi) {

    private val logger = LoggerFactory.getLogger(FinnHubClient::class.java)

    fun getMarketNewsList(category: String, minId: Long?): List<MarketNews> {
        logger.info("Fetching market news for category: $category with minId: $minId")

        val marketNewsData = apiClient.marketNews(category, minId)
        logger.debug("Raw market news data: {}", marketNewsData)

        return marketNewsData
    }
}

