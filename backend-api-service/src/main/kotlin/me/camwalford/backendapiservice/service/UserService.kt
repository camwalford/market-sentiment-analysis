package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.model.Role
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import me.camwalford.backendapiservice.model.User
import me.camwalford.backendapiservice.repository.UserRepository
import org.springframework.transaction.annotation.Transactional

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

    fun banUser(user: User) {
        logger.info("Banning user with id: ${user.id}")
        user.copy(role = Role.BANNED)
        userRepository.save(user)
    }

    @Transactional
    fun deductCredits(user: User, amount: Int) {
        logger.info("Deducting $amount credits from user with id: ${user.id}")
        if (user.credits < amount) {
            throw Exception("User does not have enough credits")
        }
        user.credits -= amount
        userRepository.save(user)
    }

    @Transactional
    fun incrementRequests(user: User) {
        logger.info("Incrementing requests for user with id: ${user.id}")
        user.requests += 1
        userRepository.save(user)
    }
}