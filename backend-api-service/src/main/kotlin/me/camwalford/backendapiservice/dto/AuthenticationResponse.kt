package me.camwalford.backendapiservice.dto
import java.io.Serializable

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
) : Serializable

