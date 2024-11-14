package me.camwalford.backendapiservice.controller.auth

import me.camwalford.backendapiservice.controller.user.UserRequest
import me.camwalford.backendapiservice.controller.user.UserResponse
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import me.camwalford.backendapiservice.service.AuthenticationService
import me.camwalford.backendapiservice.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userService: UserService
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register")
    fun create(@RequestBody userRequest: UserRequest): UserResponse {
        logger.info("Registering user with email: ${userRequest.email}")
        val user = userService.createUser(userRequest.toModel())
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create user.")
        return UserResponse.toResponse(user)
    }

    @PostMapping("/login")
    fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse {
        logger.info("Authenticating user with username: ${authRequest.username}")
        return authenticationService.authentication(authRequest)
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody request: RefreshTokenRequest): TokenResponse {
        logger.info("Refreshing access token with refresh token: ${request.token}")
        return authenticationService.refreshAccessToken(request.token)
            ?.mapToTokenResponse()
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token.")
    }

    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshTokenRequest): ResponseEntity<Void> {
        logger.info("Logging out user with refresh token: ${request.token}")
        refreshTokenRepository.deleteByToken(request.token)
        return ResponseEntity.noContent().build()
    }

    private fun String.mapToTokenResponse(): TokenResponse =
        TokenResponse(token = this)
}