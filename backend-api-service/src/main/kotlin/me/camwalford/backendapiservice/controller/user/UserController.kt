package me.camwalford.backendapiservice.controller.user

import me.camwalford.backendapiservice.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService
) {
    private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)



    @GetMapping
    fun listAll(): List<UserResponse> {
        logger.info("Listing all users")
        return userService.findAll().map { UserResponse.toResponse(it) }
    }

    @GetMapping("/email/{email}")
    fun findByEmail(@PathVariable email: String): UserResponse {
        logger.info("Finding user with email: $email")
        val user = userService.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        return UserResponse.toResponse(user)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): UserResponse {
        logger.info("Finding user with id: $id")
        val user = userService.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        return UserResponse.toResponse(user)
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("Deleting user with id: $id")
        val success = userService.deleteById(id)
        return if (success) ResponseEntity.noContent().build()
        else throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
    }
}