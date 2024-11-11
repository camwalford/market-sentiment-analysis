package me.camwalford.backendapiservice.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SentimentResult(
    @JsonProperty("date") val date: Long?,
    @JsonProperty("sentiment") val sentiment: String,  // "positive", "neutral", or "negative"
    @JsonProperty("confidence") val confidence: Float,
    @JsonProperty("ticker") val ticker: String?,
    @JsonProperty("id") val id: Long?
)
