package me.camwalford.backendapiservice.controller.user

// UserRequest.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "User creation request payload")
data class UserRequest(
    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Username",
        required = true,
        example = "john.doe"
    )
    val username: String,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Email address",
        required = true,
        example = "john.doe@example.com"
    )
    val email: String,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Password",
        required = true,
        example = "password123",
        minLength = 8
    )
    val password: String,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "User role",
        required = false,
        example = "USER",
        defaultValue = "USER"
    )
    val role: me.camwalford.backendapiservice.model.Role = me.camwalford.backendapiservice.model.Role.USER
) {
    fun toModel(): me.camwalford.backendapiservice.model.User = me.camwalford.backendapiservice.model.User(
        username = username,
        email = email,
        password = password,
        role = role
    )
}

