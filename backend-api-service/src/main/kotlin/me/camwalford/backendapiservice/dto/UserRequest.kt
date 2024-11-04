package me.camwalford.backendapiservice.dto

import me.camwalford.backendapiservice.model.User

data class UserRequest(
    val email: String,
    val password: String
) {
    fun toModel(): User = User(
        email = this.email,
        password = this.password
    )
}