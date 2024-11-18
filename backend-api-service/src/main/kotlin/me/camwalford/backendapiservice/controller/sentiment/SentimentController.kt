package me.camwalford.backendapiservice.controller.sentiment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import me.camwalford.backendapiservice.service.SentimentService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/sentiment")
@Tag(name = "Sentiment Analysis", description = "Sentiment analysis operations")
@SecurityRequirement(name = "bearer-auth")
class SentimentController(
    private val sentimentService: SentimentService
) {
    private val logger = LoggerFactory.getLogger(SentimentController::class.java)


    @PostMapping("/analyze")
    @Operation(
        summary = "Detailed sentiment analysis",
        description = "Performs detailed sentiment analysis with customizable parameters. Costs 1 credits."
    )
    fun analyzeSentiment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody request: SentimentRequest
    ): SentimentResponse {
        logger.info("Detailed sentiment analysis for ticker: ${request.ticker} by user: ${userDetails.username}")
        return sentimentService.getSentiment(userDetails, request)
    }

}