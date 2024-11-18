package me.camwalford.backendapiservice.controller.sentiment

// SentimentResult.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "Individual sentiment analysis result")
data class SentimentResult(
    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Unix timestamp of the analyzed news",
        example = "1686067200000"
    )
    @com.fasterxml.jackson.annotation.JsonProperty("date")
    val date: Long?,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Sentiment classification",
        example = "positive",
        allowableValues = ["positive", "neutral", "negative"]
    )
    @com.fasterxml.jackson.annotation.JsonProperty("sentiment")
    val sentiment: String,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Confidence score of sentiment analysis",
        example = "0.85",
        minimum = "0.0",
        maximum = "1.0"
    )
    @com.fasterxml.jackson.annotation.JsonProperty("confidence")
    val confidence: Float,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Stock ticker symbol",
        example = "AAPL"
    )
    @com.fasterxml.jackson.annotation.JsonProperty("ticker")
    val ticker: String?,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Unique identifier for this analysis",
        example = "12345"
    )
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    val id: Long?
)