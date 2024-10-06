package me.camwalford.finnhubingestionservice.client

import org.springframework.stereotype.Component
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import jakarta.annotation.PostConstruct
import me.camwalford.finnhubingestionservice.config.FinnHubConfig

@Component
class FinnHubClient(private val apiClient: DefaultApi) {

    fun getMarketNews(category: String, minId: Long?) =
        apiClient.marketNews(category, minId)

}