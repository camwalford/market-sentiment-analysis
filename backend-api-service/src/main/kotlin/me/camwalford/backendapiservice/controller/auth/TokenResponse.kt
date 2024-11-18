package me.camwalford.backendapiservice.controller.auth

// TokenResponse.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "Token response payload")
data class TokenResponse(
    @io.swagger.v3.oas.annotations.media.Schema(description = "JWT token", required = true)
    val token: String
)