package me.camwalford.backendapiservice.controller.sentiment

data class SentimentRequest(
    val ticker: String,
    val fromDate: String,
    val toDate: String
)
