package me.camwalford.backendapiservice.controller.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Endpoint statistics response")
data class EndpointStatsResponse(
    @Schema(description = "HTTP method", example = "GET")
    val method: String,

    @Schema(description = "Endpoint URI", example = "/api/v1/customers/{id}")
    val uri: String,

    @Schema(description = "Total number of requests", example = "145")
    val totalRequests: Long
)


