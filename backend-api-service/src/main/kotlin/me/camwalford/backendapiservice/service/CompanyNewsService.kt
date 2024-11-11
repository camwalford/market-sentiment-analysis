package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.dto.CompanyNews
import me.camwalford.backendapiservice.dto.SentimentRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class CompanyNewsService(
    private val webClient: WebClient,

    // Base URL for the FinnHub ingestion service (configured via environment variable)
    @Value("\${finnhub.ingestion.service.url}")
    private val finnHubIngestionServiceUrl: String
) {

    suspend fun fetchCompanyNews(request: SentimentRequest): List<CompanyNews> {
        return webClient.post()
            .uri("$finnHubIngestionServiceUrl/finnhub/company-news/list")  // Set the complete URL including endpoint
            .bodyValue(request)  // Pass request body as JSON
            .retrieve()
            .onStatus({ it.isError }) {
                throw RuntimeException("Error fetching news from FinnHub Ingestion Service")
            }
            .awaitBody()  // Asynchronously await response and parse it as List<CompanyNews>
    }
}
