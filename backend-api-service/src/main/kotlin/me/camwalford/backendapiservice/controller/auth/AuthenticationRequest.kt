package me.camwalford.backendapiservice.controller.auth

// AuthenticationRequest.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "Authentication request payload")
data class AuthenticationRequest(
    @io.swagger.v3.oas.annotations.media.Schema(description = "User's username", required = true, example = "john.doe")
    val username: String,

    @io.swagger.v3.oas.annotations.media.Schema(description = "User's email", required = false, example = "john.doe@example.com")
    val email: String?,

    @io.swagger.v3.oas.annotations.media.Schema(description = "User's password", required = true, example = "password123")
    val password: String
)