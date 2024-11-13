package me.camwalford.backendapiservice.controller.sentiment

import me.camwalford.backendapiservice.service.SentimentAnalysisService
import me.camwalford.backendapiservice.service.UserService
import me.camwalford.backendapiservice.service.CompanyNewsService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/api/sentiment")
class SentimentController(
    private val companyNewsService: CompanyNewsService,
    private val sentimentAnalysisService: SentimentAnalysisService,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(SentimentController::class.java)


    @PostMapping
    fun getSentiment(
        @AuthenticationPrincipal userDetails: UserDetails?,
        @RequestBody request: SentimentRequest
    ): ResponseEntity<SentimentResponse> {
        if (userDetails == null) {
            logger.warn("User not authenticated")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated")
        }

        logger.info("Processing sentiment request for ticker: ${request.ticker} by user: ${userDetails.username}")

        // Step 1: Validate User
        val user = userService.findByUsername(userDetails.username)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")

        // Step 2: Check User Credits
        if (user.credits <= 0) {
            throw ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Insufficient credits.")
        }

        // Step 3: Fetch News Articles
        val newsArticles = try {
            companyNewsService.fetchCompanyNews(request)
        } catch (ex: Exception) {
            logger.error("Failed to fetch news articles: ${ex.message}")
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error fetching news articles.")
        }

        // Step 4: Perform Sentiment Analysis
        val sentimentResults = try {
            sentimentAnalysisService.analyzeSentiment(newsArticles)
        } catch (ex: Exception) {
            logger.error("Sentiment analysis failed: ${ex.message}")
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Sentiment analysis failed.")
        }

        // Step 5: Deduct User Credits
        try {
            userService.deductCredits(user, 1)
        } catch (ex: Exception) {
            logger.error("Failed to deduct user credits: ${ex.message}")
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to deduct user credits.")
        }

        // Step 6: Return Sentiment Analysis Results in the Response
        val response = SentimentResponse(results = sentimentResults, creditsRemaining = user.credits)
        logger.info("Successfully processed sentiment request for ticker: ${request.ticker}")
        logger.info("User ${userDetails.username} now has ${user.credits} credits remaining")
        logger.info("Returning response: $response")
        val responseEntity = ResponseEntity.ok(response)
        logger.info("Returning response entity: $responseEntity")

        // Remove 'return' and make this the last expression
        return responseEntity
    }


}

