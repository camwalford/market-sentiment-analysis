package me.camwalford.backendapiservice.controller.sentiment

data class SentimentResponse(
    val results: List<SentimentResult>,
    val creditsRemaining: Int
)
