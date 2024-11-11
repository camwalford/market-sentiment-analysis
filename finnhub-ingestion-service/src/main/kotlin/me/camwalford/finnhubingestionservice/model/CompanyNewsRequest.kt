package me.camwalford.finnhubingestionservice.model

data class CompanyNewsRequest(
    val ticker: String,
    val fromDate: String,
    val toDate: String
)
