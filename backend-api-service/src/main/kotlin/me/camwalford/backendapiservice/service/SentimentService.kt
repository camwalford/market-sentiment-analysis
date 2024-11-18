package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.controller.sentiment.SentimentRequest
import me.camwalford.backendapiservice.controller.sentiment.SentimentResponse
import me.camwalford.backendapiservice.exception.InsufficientCreditsException
import me.camwalford.backendapiservice.exception.NewsApiException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException



@Service
class SentimentService(
    private val companyNewsService: CompanyNewsService,
    private val sentimentAnalysisService: SentimentAnalysisService,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(SentimentService::class.java)

    fun getSentiment(userDetails: UserDetails?, request: SentimentRequest): SentimentResponse {

        if (userDetails == null) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "User not authenticated"
            )
        }

        // Validate user
        val user = userService.findByUsername(userDetails.username)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "User not found"
            )

        userService.incrementRequests(user)

        // Check credits
        if (user.credits <= 0) {
            throw InsufficientCreditsException("Insufficient credits. Available credits: ${user.credits}")
        }

        // Fetch news articles
        val newsArticles = try {
            companyNewsService.fetchCompanyNews(request)
        } catch (ex: Exception) {
            logger.error("Failed to fetch news articles: ${ex.message}")
            throw NewsApiException("Error fetching news articles: ${ex.message}")
        }

        // Perform sentiment analysis
        val sentimentResults = try {
            sentimentAnalysisService.analyzeSentiment(newsArticles)
        } catch (ex: Exception) {
            logger.error("Sentiment analysis failed: ${ex.message}")
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Sentiment analysis failed: ${ex.message}"
            )
        }
        try {
            userService.deductCredits(user, 1)
        } catch (ex: Exception) {
            logger.error("Failed to decrement credits: ${ex.message}")
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to decrement credits: ${ex.message}"
            )
        }
        val sentimentResponse = SentimentResponse(sentimentResults, user.credits - 1)
        return sentimentResponse
    }

}

