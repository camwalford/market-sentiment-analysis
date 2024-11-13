package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.controller.sentiment.CompanyNews
import me.camwalford.backendapiservice.controller.sentiment.SentimentResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class SentimentAnalysisService(
    private val webClient: WebClient,
    @Value("\${sentiment.analysis.service.url}") private val sentimentAnalysisServiceUrl: String
) {
    fun analyzeSentiment(newsArticles: List<CompanyNews>): List<SentimentResult> {
        return webClient.post()
            .uri("$sentimentAnalysisServiceUrl/analyze-list")
            .bodyValue(newsArticles)
            .retrieve()
            .bodyToMono<List<SentimentResult>>()
            .block() ?: throw RuntimeException("Error in Sentiment Analysis Service")
    }
}