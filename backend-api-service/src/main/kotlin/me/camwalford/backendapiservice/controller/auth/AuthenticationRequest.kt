package me.camwalford.backendapiservice.controller.auth

data class AuthenticationRequest(
    val username: String,
    val email: String?,
    val password: String,
)
