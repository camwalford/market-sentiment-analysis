package me.camwalford.finnhubingestionservice.service

import me.camwalford.finnhubingestionservice.client.FinnHubClient
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FinnHubService(private val finnHubClient: FinnHubClient) {

    private val logger = LoggerFactory.getLogger(FinnHubService::class.java)

    fun fetchMarketNews(category: String = "general", minId: Long? = 0L): MarketNewsList {
        logger.info("Fetching market news from FinnHubClient for category: $category, minId: $minId")
        return finnHubClient.getMarketNews(category, minId)
    }
}
