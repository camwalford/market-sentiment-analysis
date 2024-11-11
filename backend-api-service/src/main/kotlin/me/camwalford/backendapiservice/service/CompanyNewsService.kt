package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.dto.CompanyNews
import me.camwalford.backendapiservice.dto.SentimentRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class CompanyNewsService(
    private val webClient: WebClient,
    @Value("\${finnhub.ingestion.service.url}") private val companyNewsServiceUrl: String
) {
    fun fetchCompanyNews(request: SentimentRequest): List<CompanyNews> {
        return webClient.post()
            .uri("$companyNewsServiceUrl/finnhub/company-news/list")
            .bodyValue(request)
            .retrieve()
            .bodyToMono<List<CompanyNews>>()
            .block() ?: throw RuntimeException("Error in Company News Service")
    }
}