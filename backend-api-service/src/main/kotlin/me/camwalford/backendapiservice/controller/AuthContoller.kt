package me.camwalford.backendapiservice.controller

import me.camwalford.backendapiservice.dto.AuthenticationRequest
import me.camwalford.backendapiservice.dto.AuthenticationResponse
import me.camwalford.backendapiservice.dto.RefreshTokenRequest
import me.camwalford.backendapiservice.dto.TokenResponse
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import me.camwalford.backendapiservice.service.AuthenticationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse {
        logger.info("Authenticating user with email: ${authRequest.email}")
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