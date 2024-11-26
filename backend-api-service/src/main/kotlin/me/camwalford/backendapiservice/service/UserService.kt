package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.exception.*
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

    fun createUser(user: User): User {
        logger.info("Creating user with email: ${user.email}")

        // Check email
        userRepository.findByEmail(user.email)?.let {
            throw DuplicateUserException(
                field = "email",
                value = user.email,
                details = mapOf(
                    "email" to user.email,
                    "attemptedAt" to System.currentTimeMillis()
                )
            )
        }

        // Check username
        userRepository.findUserByUsername(user.username)?.let {
            throw DuplicateUserException(
                field = "username",
                value = user.username,
                details = mapOf(
                    "username" to user.username,
                    "attemptedAt" to System.currentTimeMillis()
                )
            )
        }

        val hashedPassword = passwordEncoder.encode(user.password)
        val userWithHashedPassword = user.copy(password = hashedPassword)
        val savedUser = userRepository.save(userWithHashedPassword)
        logger.info("User created with id: ${savedUser.id}")
        return savedUser
    }

    fun isUsernameAvailable(username: String): Boolean {
        logger.info("Checking if username is available: $username")
        if (username.isBlank()) {
            throw InvalidUserOperationException(
                operation = "username validation",
                reason = "Username cannot be blank",
                details = mapOf("username" to username)
            )
        }
        return userRepository.findUserByUsername(username) == null
    }

    fun isEmailAvailable(email: String): Boolean {
        logger.info("Checking if email is available: $email")
        if (email.isBlank()) {
            throw InvalidUserOperationException(
                operation = "email validation",
                reason = "Email cannot be blank",
                details = mapOf("email" to email)
            )
        }
        return userRepository.findByEmail(email) == null
    }

    fun findByEmail(email: String): User {
        logger.info("Finding user with email: $email")
        return userRepository.findByEmail(email) ?: throw UserNotFoundException(
            identifier = email,
            details = mapOf(
                "email" to email,
                "searchType" to "email"
            )
        )
    }

    fun findByUsername(username: String): User {
        logger.info("Finding user with username: $username")
        return userRepository.findUserByUsername(username) ?: throw UserNotFoundException(
            identifier = username,
            details = mapOf(
                "username" to username,
                "searchType" to "username"
            )
        )
    }

    fun findById(id: Long): User {
        logger.info("Finding user with id: $id")
        return userRepository.findById(id).orElseThrow {
            UserNotFoundException(
                identifier = id,
                details = mapOf(
                    "userId" to id,
                    "searchType" to "id"
                )
            )
        }
    }

    fun findAll(): List<User> {
        logger.info("Finding all users")
        return userRepository.findAll()
    }

    fun deleteById(id: Long): Boolean {
        logger.info("Deleting user with id: $id")
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException(
                identifier = id,
                details = mapOf(
                    "userId" to id,
                    "operation" to "delete"
                )
            )
        }
        userRepository.deleteById(id)
        logger.info("User with id $id deleted")
        return true
    }

    fun banUser(user: User) {
        logger.info("Banning user with id: ${user.id}")
        if (user.role == Role.BANNED) {
            throw InvalidUserOperationException(
                operation = "ban user",
                reason = "User is already banned",
                details = mapOf(
                    "userId" to user.id,
                    "username" to user.username,
                    "currentRole" to user.role
                )
            )
        }

        val bannedUser = user.copy(role = Role.BANNED)
        userRepository.save(bannedUser)
        logger.info("User ${user.username} has been banned")
    }

    @Transactional
    fun deductCredits(user: User, amount: Int) {
        logger.info("Deducting $amount credits from user with id: ${user.id}")

        // Check if user is banned
        if (user.role == Role.BANNED) {
            throw UserBannedException(
                username = user.username,
                reason = "Cannot deduct credits from banned user",
                details = mapOf(
                    "userId" to user.id,
                    "username" to user.username,
                    "attemptedDeduction" to amount
                )
            )
        }

        // Check if user has enough credits
        if (user.credits < amount) {
            throw InsufficientCreditsException(
                current = user.credits,
                required = amount
            )
        }

        user.credits -= amount
        userRepository.save(user)
        logger.info("Successfully deducted $amount credits from user ${user.username}")
    }

    fun addCredits(user: User, amount: Int) {
        logger.info("Adding $amount credits to user with id: ${user.id}")

        if (amount <= 0) {
            throw InvalidUserOperationException(
                operation = "add credits",
                reason = "Amount must be positive",
                details = mapOf(
                    "userId" to user.id,
                    "attemptedAmount" to amount
                )
            )
        }

        if (user.role == Role.BANNED) {
            throw UserBannedException(
                username = user.username,
                reason = "Cannot add credits to banned user"
            )
        }

        user.credits += amount
        userRepository.save(user)
        logger.info("Successfully added $amount credits to user ${user.username}")
    }
}