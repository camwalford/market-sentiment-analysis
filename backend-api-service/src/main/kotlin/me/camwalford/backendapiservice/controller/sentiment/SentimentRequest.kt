package me.camwalford.backendapiservice.controller.sentiment

// SentimentRequest.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "Request payload for sentiment analysis of company news")
data class SentimentRequest(
    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Stock ticker symbol",
        example = "AAPL",
        required = true
    )
    var ticker: String,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Start date for analysis period (YYYY-MM-DD)",
        example = "2024-06-06",
        required = true
    )
    val fromDate: String,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "End date for analysis period (YYYY-MM-DD)",
        example = "2024-07-06",
        required = true
    )
    val toDate: String
)