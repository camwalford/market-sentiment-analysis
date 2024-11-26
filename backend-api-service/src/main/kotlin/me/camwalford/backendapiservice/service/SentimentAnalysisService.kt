package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.controller.sentiment.CompanyNews
import me.camwalford.backendapiservice.controller.sentiment.SentimentResult
import me.camwalford.backendapiservice.exception.SentimentAnalysisException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class SentimentAnalysisService(
    private val webClient: WebClient,
    @Value("\${sentiment.analysis.service.url}") private val sentimentAnalysisServiceUrl: String
) {
    private val logger = LoggerFactory.getLogger(SentimentAnalysisService::class.java)

    fun analyzeSentiment(newsArticles: List<CompanyNews>): List<SentimentResult> {
        logger.info("Analyzing sentiment for ${newsArticles.size} news articles")

        try {
            return webClient.post()
                .uri("$sentimentAnalysisServiceUrl/analyze-list")
                .bodyValue(newsArticles)
                .retrieve()
                .onStatus({ status -> status.is5xxServerError }) { response ->
                    throw SentimentAnalysisException(
                        "Sentiment analysis service unavailable",
                        mapOf(
                            "statusCode" to response.statusCode().value(),
                            "articlesCount" to newsArticles.size
                        )
                    )
                }
                .bodyToMono<List<SentimentResult>>()
                .block() ?: throw SentimentAnalysisException(
                "No response from sentiment analysis service",
                mapOf("articlesCount" to newsArticles.size)
            )
        } catch (ex: Exception) {
            when (ex) {
                is SentimentAnalysisException -> throw ex
                else -> throw SentimentAnalysisException(
                    "Error analyzing sentiment: ${ex.message}",
                    mapOf(
                        "error" to (ex.message ?: "Unknown error"),
                        "articlesCount" to newsArticles.size
                    )
                )
            }
        }
    }
}