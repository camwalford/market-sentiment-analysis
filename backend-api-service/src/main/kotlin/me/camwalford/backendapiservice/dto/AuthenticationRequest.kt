package me.camwalford.backendapiservice.dto

data class AuthenticationRequest(
    val username: String,
    val email: String?,
    val password: String,
)
