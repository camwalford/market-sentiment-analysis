package me.camwalford.finnhubingestionservice.model

data class MarketNewsResponse(
    val category: String,
    val headline: String,
    val source: String,
    val summary: String,
    val url: String,
    val related: String,
    val datetime: Long,
    val image: String
)
