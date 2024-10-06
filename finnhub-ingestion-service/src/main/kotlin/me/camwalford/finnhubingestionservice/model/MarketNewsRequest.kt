package me.camwalford.finnhubingestionservice.model

data class MarketNewsRequest(
    val category: String,
    val minId: Long?
)