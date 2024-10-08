package me.camwalford.finnhubingestionservice.config

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration  // Change this from @Component to @Configuration
class FinnHubConfig(private val finnHubApiConfig: FinnHubApiConfig) {

    @Bean
    fun defaultApi(): DefaultApi {
        ApiClient.apiKey["token"] = finnHubApiConfig.apiKey
        return DefaultApi()
    }
}