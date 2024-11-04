package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository
) {

    fun decrementUserCredit(username: String): Boolean {
        val user = userRepository.findByUsername(username) ?: return false
        if (user.credits > 0) {
            user.credits -= 1
            userRepository.save(user)
            return true
        }
        return false
    }

    // Additional methods as needed
}