package me.camwalford.backendapiservice.controller.user

import io.swagger.v3.oas.annotations.Operation

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import me.camwalford.backendapiservice.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/user")
@Tag(name = "Users", description = "User management operations")
class UserController(
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/me")
    @Operation(
        summary = "Get current user",
        description = "Returns the profile of the currently authenticated user",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    fun getCurrentUser(@AuthenticationPrincipal userDetails: UserDetails): UserResponse {
        logger.info("Fetching current user profile: ${userDetails.username}")
        return userService.findByUsername(userDetails.username)
            ?.let { UserResponse.toResponse(it) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    }


    // Admin endpoints below
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "List all users",
        description = "Returns a list of all users. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    fun listAll(): List<UserResponse> {
        logger.info("Admin: Listing all users")
        return userService.findAll().map { UserResponse.toResponse(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get user by ID",
        description = "Returns a specific user by ID. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    fun getUserById(@PathVariable id: Long): UserResponse {
        logger.info("Admin: Fetching user with ID: $id")
        return userService.findById(id)
            ?.let { UserResponse.toResponse(it) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete user",
        description = "Deletes a user account. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("Admin: Deleting user with ID: $id")
        return if (userService.deleteById(id)) {
            ResponseEntity.noContent().build()
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }
    }
}