package me.camwalford.backendapiservice.controller.sentiment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import me.camwalford.backendapiservice.service.SentimentService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory
import org.springframework.http.ProblemDetail

@RestController
@RequestMapping("/api/sentiment")
@Tag(name = "Sentiment Analysis", description = "Sentiment analysis operations")
@SecurityRequirement(name = "bearer-auth")
class SentimentController(
    private val sentimentService: SentimentService
) {
    private val logger = LoggerFactory.getLogger(SentimentController::class.java)

    @PostMapping("/analyze")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Detailed sentiment analysis",
        description = "Performs detailed sentiment analysis with customizable parameters. Costs 1 credit."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Analysis completed successfully",
            content = [Content(schema = Schema(implementation = SentimentResponse::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "invalidDate",
                        summary = "Invalid date range",
                        description = "Example of error when date range is invalid",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/DATE_001",
                            "title": "Invalid Date Range",
                            "status": 400,
                            "detail": "Date range cannot exceed 365 days",
                            "errorCode": "DATE_001"
                        }
                        """
                    ),
                    ExampleObject(
                        name = "invalidTicker",
                        summary = "Invalid ticker symbol",
                        description = "Example of error when ticker symbol is invalid",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/TICKER_001",
                            "title": "Invalid Ticker",
                            "status": 400,
                            "detail": "Invalid ticker symbol provided",
                            "errorCode": "TICKER_001"
                        }
                        """
                    )
                ]
            )]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            ref = "#/components/responses/UnauthorizedError"
        ),
        ApiResponse(
            responseCode = "402",
            description = "Insufficient credits",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "insufficientCredits",
                        summary = "Insufficient credits",
                        description = "Example of error when user has insufficient credits",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/USER_003",
                            "title": "Insufficient Credits",
                            "status": 402,
                            "detail": "Insufficient credits: has 0, needs 1",
                            "errorCode": "USER_003",
                            "currentCredits": 0,
                            "requiredCredits": 1
                        }
                        """
                    )
                ]
            )]
        ),
        ApiResponse(
            responseCode = "403",
            description = "User is banned",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "bannedUser",
                        summary = "Banned user",
                        description = "Example of error when banned user attempts analysis",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/USER_004",
                            "title": "User Banned",
                            "status": 403,
                            "detail": "Cannot perform analysis - user is banned",
                            "errorCode": "USER_004"
                        }
                        """
                    )
                ]
            )]
        ),
        ApiResponse(
            responseCode = "503",
            description = "Service unavailable",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "serviceUnavailable",
                        summary = "Service unavailable",
                        description = "Example of error when external service is unavailable",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/SENTIMENT_001",
                            "title": "Sentiment Analysis Service Error",
                            "status": 503,
                            "detail": "Sentiment analysis service is currently unavailable",
                            "errorCode": "SENTIMENT_001"
                        }
                        """
                    )
                ]
            )]
        )
    ])
    fun analyzeSentiment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody request: SentimentRequest
    ): SentimentResponse {
        logger.info("Detailed sentiment analysis for ticker: ${request.ticker} by user: ${userDetails.username}")
        return sentimentService.getSentiment(userDetails, request)
    }

}