package me.camwalford.backendapiservice.dto

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)
