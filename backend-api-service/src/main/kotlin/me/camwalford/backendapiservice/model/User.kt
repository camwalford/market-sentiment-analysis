package me.camwalford.backendapiservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(unique = true, nullable = false)
    val username: String = "",

    @Column(nullable = false)
    val password: String = "", // Hashed with bcrypt

    @Column(nullable = false)
    val role: Role = Role.USER, // "admin" or "user"

    @Column(nullable = false)
    var credits: Int = 20 // Default to 20 credits
)

enum class Role {
    USER, ADMIN
}