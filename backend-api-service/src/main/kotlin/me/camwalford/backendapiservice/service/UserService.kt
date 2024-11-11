package me.camwalford.backendapiservice.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import me.camwalford.backendapiservice.model.User
import me.camwalford.backendapiservice.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    fun createUser(user: User): User? {
        logger.info("Creating user with email: ${user.email}")
        if (userRepository.findByEmail(user.email) != null) {
            logger.warn("User with email ${user.email} already exists")
            return null
        }
        if (userRepository.findUserByUsername(user.username) != null) {
            logger.warn("User with username ${user.username} already exists")
            return null
        }
        val hashedPassword = passwordEncoder.encode(user.password)
        val userWithHashedPassword = user.copy(password = hashedPassword)
        val savedUser = userRepository.save(userWithHashedPassword)
        logger.info("User created with id: ${savedUser.id}")
        return savedUser
    }

    fun findByEmail(email: String): User? {
        logger.info("Finding user with email: $email")
        return userRepository.findByEmail(email)
    }

    fun findByUsername(username: String): User? {
        logger.info("Finding user with username: $username")
        return userRepository.findUserByUsername(username)
    }

    fun findById(id: Long): User? {
        logger.info("Finding user with id: $id")
        return userRepository.findById(id).orElse(null)
    }

    fun findAll(): List<User> {
        logger.info("Finding all users")
        return userRepository.findAll()
    }

    fun deleteById(id: Long): Boolean {
        logger.info("Deleting user with id: $id")
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            logger.info("User with id $id deleted")
            true
        } else {
            logger.warn("User with id $id does not exist")
            false
        }
    }

    fun deductCredits(user: User, credits: Int): User {
        logger.info("Deducting $credits credits from user with id: ${user.id}")
        val updatedUser = user.copy(credits = user.credits - credits)
        val savedUser = userRepository.save(updatedUser)
        logger.info("Deducted $credits credits from user with id: ${user.id}")
        return savedUser
    }
}