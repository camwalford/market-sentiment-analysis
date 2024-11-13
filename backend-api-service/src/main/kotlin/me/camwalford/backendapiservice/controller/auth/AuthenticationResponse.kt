package me.camwalford.backendapiservice.controller.auth
import me.camwalford.backendapiservice.controller.user.UserResponse
import java.io.Serializable

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
) : Serializable

