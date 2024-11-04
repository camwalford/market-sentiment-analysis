package me.camwalford.backendapiservice.repository

// src/main/kotlin/com/example/demo/repository/UserRepository.kt


import me.camwalford.backendapiservice.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}
