package me.camwalford.backendapiservice.dto

data class SentimentRequest(
    val ticker: String,
    val fromDate: String,
    val toDate: String
)
