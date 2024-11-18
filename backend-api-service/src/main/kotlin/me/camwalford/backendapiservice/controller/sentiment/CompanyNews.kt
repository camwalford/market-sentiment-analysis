package me.camwalford.backendapiservice.controller.sentiment

import java.io.Serializable

// CompanyNews.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "Company news article details")
data class CompanyNews(
    @io.swagger.v3.oas.annotations.media.Schema(description = "News category", example = "Technology")
    @com.fasterxml.jackson.annotation.JsonProperty("category")
    val category: String?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "Unix timestamp of news publication", example = "1686067200000")
    @com.fasterxml.jackson.annotation.JsonProperty("datetime")
    val datetime: Long?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "News headline", example = "Apple Announces New iPhone Features")
    @com.fasterxml.jackson.annotation.JsonProperty("headline")
    val headline: String?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "Unique news article ID", example = "12345")
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    val id: Long?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "URL of the news article image", example = "https://example.com/image.jpg")
    @com.fasterxml.jackson.annotation.JsonProperty("image")
    val image: String?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "Related company ticker symbol", example = "AAPL")
    @com.fasterxml.jackson.annotation.JsonProperty("related")
    val related: String?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "News source name", example = "Reuters")
    @com.fasterxml.jackson.annotation.JsonProperty("source")
    val source: String?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "News article summary", example = "Apple Inc. announced new features coming to iPhone...")
    @com.fasterxml.jackson.annotation.JsonProperty("summary")
    val summary: String?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "News article URL", example = "https://example.com/article")
    @com.fasterxml.jackson.annotation.JsonProperty("url")
    val url: String?
)