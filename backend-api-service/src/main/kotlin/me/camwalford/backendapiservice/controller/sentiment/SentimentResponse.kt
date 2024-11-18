package me.camwalford.backendapiservice.controller.sentiment

// SentimentResponse.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "Response payload containing sentiment analysis results")
data class SentimentResponse(
    @io.swagger.v3.oas.annotations.media.Schema(description = "List of sentiment analysis results")
    val results: List<SentimentResult>,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Number of API credits remaining for the user",
        example = "95"
    )
    val creditsRemaining: Int
)