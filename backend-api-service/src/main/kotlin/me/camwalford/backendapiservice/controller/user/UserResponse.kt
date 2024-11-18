package me.camwalford.backendapiservice.controller.user

// UserResponse.kt
@io.swagger.v3.oas.annotations.media.Schema(description = "User response payload")
data class UserResponse(
    @io.swagger.v3.oas.annotations.media.Schema(description = "User ID", example = "1")
    val id: Long,

    @io.swagger.v3.oas.annotations.media.Schema(description = "Username", example = "john.doe")
    val username: String,

    @io.swagger.v3.oas.annotations.media.Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "User role",
        example = "USER",
        allowableValues = ["USER", "ADMIN"]
    )
    val role: me.camwalford.backendapiservice.model.Role,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Available API credits",
        example = "100",
        minimum = "0"
    )
    val credits: Int,

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Number of API requests made",
        example = "5",
        minimum = "0"
    )
    val requests: Int
) : java.io.Serializable {
    companion object {
        fun toResponse(user: me.camwalford.backendapiservice.model.User): UserResponse = UserResponse(
            id = user.id,
            username = user.username,
            email = user.email,
            role = user.role,
            credits = user.credits,
            requests = user.requests
        )
    }
}