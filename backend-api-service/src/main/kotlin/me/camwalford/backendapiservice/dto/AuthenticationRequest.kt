package me.camwalford.backendapiservice.dto

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
