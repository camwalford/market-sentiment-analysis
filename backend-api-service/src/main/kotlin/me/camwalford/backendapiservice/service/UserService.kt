package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.model.User
import me.camwalford.backendapiservice.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder

) {

    fun createUser(user: User): User? {
        if (userRepository.findByEmail(user.email) != null) return null
        val hashedPassword = passwordEncoder.encode(user.password)
        val userWithHashedPassword = user.copy(password = hashedPassword)
        return userRepository.save(userWithHashedPassword)
    }

    fun findById(id: Long): User? =
        userRepository.findById(id).orElse(null)

    fun findAll(): List<User> =
        userRepository.findAll()

    fun deleteById(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}