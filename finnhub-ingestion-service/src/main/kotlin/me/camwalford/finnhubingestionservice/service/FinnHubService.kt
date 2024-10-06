package me.camwalford.finnhubingestionservice.service

import me.camwalford.finnhubingestionservice.client.FinnHubClient
import org.springframework.stereotype.Service

@Service
class FinnHubService(private val finnHubClient: FinnHubClient) {
    fun fetchMarketNews(category: String = "general", minId: Long? = 0L) =
        finnHubClient.getMarketNews(category, minId)
}