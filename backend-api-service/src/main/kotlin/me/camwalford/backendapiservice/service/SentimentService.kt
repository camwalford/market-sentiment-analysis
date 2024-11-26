package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.controller.sentiment.SentimentRequest
import me.camwalford.backendapiservice.controller.sentiment.SentimentResponse
import me.camwalford.backendapiservice.exception.InsufficientCreditsException
import me.camwalford.backendapiservice.exception.InvalidDateRangeException
import me.camwalford.backendapiservice.exception.InvalidTickerException
import me.camwalford.backendapiservice.exception.InvalidUserOperationException
import me.camwalford.backendapiservice.exception.NewsServiceException
import me.camwalford.backendapiservice.exception.SentimentAnalysisException
import me.camwalford.backendapiservice.exception.UserAuthenticationException
import me.camwalford.backendapiservice.exception.UserBannedException
import me.camwalford.backendapiservice.exception.UserNotFoundException
import me.camwalford.backendapiservice.model.Role
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant


@Service
class SentimentService(
    private val companyNewsService: CompanyNewsService,
    private val sentimentAnalysisService: SentimentAnalysisService,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(SentimentService::class.java)

    fun getSentiment(userDetails: UserDetails?, request: SentimentRequest): SentimentResponse {
        logger.info("Processing sentiment analysis request for ticker: ${request.ticker}")

        // Validate user authentication
        if (userDetails == null) {
            throw UserAuthenticationException(
                "User not authenticated",
                details = mapOf(
                    "requestedTicker" to request.ticker,
                    "timestamp" to Instant.now().toString()
                )
            )
        }

        // Get user
        val user = try {
            userService.findByUsername(userDetails.username)
        } catch (ex: UserNotFoundException) {
            throw UserAuthenticationException(
                "User not found: ${userDetails.username}",
                details = mapOf(
                    "username" to userDetails.username,
                    "requestedTicker" to request.ticker
                )
            )
        }

        // Check if user is banned
        if (user.role == Role.BANNED) {
            throw UserBannedException(
                username = user.username,
                reason = "Cannot perform sentiment analysis - user is banned",
                details = mapOf(
                    "requestedTicker" to request.ticker,
                    "userId" to user.id
                )
            )
        }

        // Check credits
        if (user.credits <= 0) {
            throw InsufficientCreditsException(
                current = user.credits,
                required = 1
            )
        }

        // Fetch news articles
        val newsArticles = try {
            logger.debug("Fetching news articles for ticker: ${request.ticker}")
            companyNewsService.fetchCompanyNews(request)
        } catch (ex: Exception) {
            when (ex) {
                is NewsServiceException -> throw ex
                is InvalidTickerException -> throw ex
                is InvalidDateRangeException -> throw ex
                else -> throw NewsServiceException(
                    "Failed to fetch news articles",
                    mapOf(
                        "error" to (ex.message ?: "Unknown error"),
                        "ticker" to request.ticker,
                        "fromDate" to request.fromDate,
                        "toDate" to request.toDate
                    )
                )
            }
        }

        // Validate news articles
        if (newsArticles.isEmpty()) {
            throw NewsServiceException(
                "No news articles found for the specified period",
                mapOf(
                    "ticker" to request.ticker,
                    "fromDate" to request.fromDate,
                    "toDate" to request.toDate
                )
            )
        }

        // Perform sentiment analysis
        val sentimentResults = try {
            logger.debug("Performing sentiment analysis for ${newsArticles.size} articles")
            sentimentAnalysisService.analyzeSentiment(newsArticles)
        } catch (ex: Exception) {
            when (ex) {
                is SentimentAnalysisException -> throw ex
                else -> throw SentimentAnalysisException(
                    "Sentiment analysis failed",
                    mapOf(
                        "error" to (ex.message ?: "Unknown error"),
                        "ticker" to request.ticker,
                        "articlesCount" to newsArticles.size
                    )
                )
            }
        }

        // Deduct credits
        try {
            logger.debug("Deducting credits for user: ${user.username}")
            userService.deductCredits(user, 1)
        } catch (ex: Exception) {
            when (ex) {
                is InsufficientCreditsException -> throw ex
                is UserBannedException -> throw ex
                else -> throw InvalidUserOperationException(
                    operation = "credit deduction",
                    reason = "Failed to deduct credits: ${ex.message}",
                    details = mapOf(
                        "userId" to user.id,
                        "username" to user.username,
                        "currentCredits" to user.credits,
                        "deductionAmount" to 1
                    )
                )
            }
        }

        logger.info("Successfully completed sentiment analysis for ticker: ${request.ticker}")
        return SentimentResponse(
            results = sentimentResults,
            creditsRemaining = user.credits
        )
    }
}

