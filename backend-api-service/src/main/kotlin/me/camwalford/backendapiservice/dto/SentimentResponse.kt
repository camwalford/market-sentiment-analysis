package me.camwalford.backendapiservice.dto

import me.camwalford.backendapiservice.model.SentimentData

data class SentimentResponse(
    val results: List<SentimentResult>,
    val creditsRemaining: Int
)
