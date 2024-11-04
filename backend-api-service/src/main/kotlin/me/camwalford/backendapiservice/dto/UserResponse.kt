package me.camwalford.backendapiservice.dto

import me.camwalford.backendapiservice.model.User
import me.camwalford.backendapiservice.model.Role
import java.io.Serializable

data class UserResponse(
    val id: Long,
    val email: String,
    val role: Role,
    val credits: Int
) : Serializable {
    companion object {
        fun toResponse(user: User): UserResponse = UserResponse(
            id = user.id,
            email = user.email,
            role = user.role,
            credits = user.credits
        )
    }
}