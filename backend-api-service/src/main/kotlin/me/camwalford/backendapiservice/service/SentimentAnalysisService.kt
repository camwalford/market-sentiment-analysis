package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.dto.CompanyNews
import me.camwalford.backendapiservice.dto.SentimentResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class SentimentAnalysisService(
    private val webClient: WebClient,
    @Value("\${sentiment.analysis.service.url}") private val sentimentAnalysisServiceUrl: String
) {
    suspend fun analyzeSentiment(newsArticles: List<CompanyNews>): List<SentimentResult> {
        return webClient.post()
            .uri("$sentimentAnalysisServiceUrl/analyze")
            .bodyValue(newsArticles)
            .retrieve()
            .onStatus({ it.isError }) {
                throw RuntimeException("Error in Sentiment Analysis Service")
            }
            .awaitBody()  // Asynchronously awaits response and converts it to List<SentimentResult>
    }
}
