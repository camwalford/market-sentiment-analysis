package me.camwalford.backendapiservice.dto

data class SentimentRequest(
    val ticker: String,
    val startDate: String,
    val endDate: String
)
