package me.camwalford.backendapiservice.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class CompanyNews(
    @JsonProperty("category") val category: String?,
    @JsonProperty("datetime") val datetime: Long?,
    @JsonProperty("headline") val headline: String?,
    @JsonProperty("id") val id: Long?,
    @JsonProperty("image") val image: String?,
    @JsonProperty("related") val related: String?,  // Company ticker
    @JsonProperty("source") val source: String?,
    @JsonProperty("summary") val summary: String?,
    @JsonProperty("url") val url: String?
) : Serializable
