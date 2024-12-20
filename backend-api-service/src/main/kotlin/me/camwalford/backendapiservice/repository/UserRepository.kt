package me.camwalford.backendapiservice.repository


import me.camwalford.backendapiservice.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>{
    fun findByEmail(email: String): User?
    fun findUserByUsername(username: String): User?

}
