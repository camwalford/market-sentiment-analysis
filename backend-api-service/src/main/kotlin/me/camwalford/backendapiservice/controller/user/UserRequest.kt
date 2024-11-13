package me.camwalford.backendapiservice.controller.user

import me.camwalford.backendapiservice.model.User

data class UserRequest(
    val username: String,
    val email: String,
    val password: String
) {
    fun toModel(): User = User(
        username = this.username,
        email = this.email,
        password = this.password
    )
}