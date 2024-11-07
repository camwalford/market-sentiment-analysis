package me.camwalford.backendapiservice.dto

import me.camwalford.backendapiservice.model.SentimentData

data class SentimentResponse(
    val ticker: String,
    val sentimentData: List<SentimentData>
)
