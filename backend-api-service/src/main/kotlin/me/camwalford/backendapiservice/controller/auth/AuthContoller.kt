package me.camwalford.backendapiservice.controller.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory
import me.camwalford.backendapiservice.controller.user.UserRequest
import me.camwalford.backendapiservice.controller.user.UserResponse
import me.camwalford.backendapiservice.service.AuthenticationService
import me.camwalford.backendapiservice.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization operations")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val userService: UserService,
    private val request: HttpServletRequest
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register")
    @Operation(
        summary = "Register new user",
        description = "Creates a new user account in the system. No authentication required."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully registered user",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = UserResponse::class)
            )]
        ),
        ApiResponse(responseCode = "400", description = "Invalid request - Username/email already exists or invalid data")
    ])
    fun register(@RequestBody userRequest: UserRequest): UserResponse {
        logger.info("Registering new user with email: ${userRequest.email}")
        return userService.createUser(userRequest.toModel())
            ?.let { UserResponse.toResponse(it) }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create user")
    }

    @GetMapping("/check-username/{username}")
    @Operation(
        summary = "Check username availability",
        description = "Checks if a username is available for registration. No authentication required."
    )
    fun checkUsername(@PathVariable username: String): Boolean {
        logger.info("Checking username availability: $username")
        return userService.isUsernameAvailable(username)
    }

    @GetMapping("/check-email/{email}")
    @Operation(
        summary = "Check email availability",
        description = "Checks if an email is available for registration. No authentication required."
    )
    fun checkEmail(@PathVariable email: String): Boolean {
        logger.info("Checking email availability: $email")
        return userService.isEmailAvailable(email)
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticates user and returns tokens in HTTP-only cookies. No authentication required."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = [Content(schema = Schema(implementation = AuthenticationResponse::class))]
        ),
        ApiResponse(responseCode = "401", description = "Invalid credentials")
    ])
    fun login(@RequestBody request: AuthenticationRequest): AuthenticationResponse {
        logger.info("Login attempt for username: ${request.username}")
        return authenticationService.authentication(request)
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh token",
        description = "Generates new access token using refresh token from cookie. Requires valid refresh token."
    )
    fun refreshToken(): TokenResponse {
        val refreshToken = request.cookies?.find { it.name == "refresh_token" }?.value
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token not found")

        return authenticationService.refreshAccessToken(refreshToken)
            ?.let { TokenResponse(it) }
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token")
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Logout",
        description = "Invalidates tokens and clears authentication cookies. Requires authentication."
    )
    fun logout(): ResponseEntity<Void> {
        authenticationService.logout(request)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate session", description = "Validates the current session and returns user data")
    fun validateSession(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<UserResponse> {
        if (userDetails == null) {
            logger.error("No userDetails found in the authentication principal.")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val user = userService.findByUsername(userDetails.username)
        if (user == null) {
            logger.warn("User not found for username: ${userDetails.username}")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        logger.info("Session validated for user: ${user.username}")
        return ResponseEntity.ok(UserResponse.toResponse(user))
    }

}