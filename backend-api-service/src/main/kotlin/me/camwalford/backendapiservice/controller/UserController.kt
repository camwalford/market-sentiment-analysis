// src/main/kotlin/me/camwalford/backendapiservice/controller/UserController.kt

package me.camwalford.backendapiservice.controller

import me.camwalford.backendapiservice.dto.UserRequest
import me.camwalford.backendapiservice.dto.UserResponse
import me.camwalford.backendapiservice.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun create(@RequestBody userRequest: UserRequest): UserResponse {
        val user = userService.createUser(userRequest.toModel())
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create user.")
        return UserResponse.toResponse(user)
    }

    @GetMapping
    fun listAll(): List<UserResponse> =
        userService.findAll()
            .map { UserResponse.toResponse(it) }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): UserResponse {
        val user = userService.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        return UserResponse.toResponse(user)
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long): ResponseEntity<Void> {
        val success = userService.deleteById(id)
        return if (success) ResponseEntity.noContent().build()
        else throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
    }
}
