package me.camwalford.backendapiservice.dto

data class SentimentRequest(
    val tickers: List<String>,
    val startDate: String,
    val endDate: String
)
