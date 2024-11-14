package me.camwalford.backendapiservice.controller.sentiment

import me.camwalford.backendapiservice.service.SentimentAnalysisService
import me.camwalford.backendapiservice.service.UserService
import me.camwalford.backendapiservice.service.CompanyNewsService
import me.camwalford.backendapiservice.service.SentimentService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/api/sentiment")
class SentimentController(
    private val sentimentService: SentimentService
) {
    private val logger = LoggerFactory.getLogger(SentimentController::class.java)

    @PostMapping
    suspend fun getSentiment(
        @AuthenticationPrincipal userDetails: UserDetails?,
        @RequestBody request: SentimentRequest
    ): ResponseEntity<SentimentResponse> {
        logger.info("Received sentiment request for ticker: ${request.ticker} by user: ${userDetails?.username}")
        return ResponseEntity.ok(sentimentService.getSentiment(userDetails, request))
    }
}


