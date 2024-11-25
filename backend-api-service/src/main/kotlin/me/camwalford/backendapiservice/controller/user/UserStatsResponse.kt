package me.camwalford.backendapiservice.controller.user

import io.swagger.v3.oas.annotations.media.Schema
import me.camwalford.backendapiservice.model.Role

@Schema(description = "User statistics response")
data class UserStatsResponse(
    @Schema(description = "User ID", example = "1")
    val userId: Long,

    @Schema(description = "Username", example = "john.doe")
    val username: String,

    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,

    @Schema(description = "User role", example = "USER")
    val role: String,

    @Schema(description = "Total number of credits", example = "20")
    val credits: Int,

    @Schema(description = "Total number of API requests made", example = "143")
    val totalRequests: Long
)
