package me.camwalford.backendapiservice.controller.auth

// AuthenticationResponse.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "Authentication response payload")
data class AuthenticationResponse(
    @io.swagger.v3.oas.annotations.media.Schema(description = "User details", required = true)
    val user: me.camwalford.backendapiservice.controller.user.UserResponse
) : java.io.Serializable