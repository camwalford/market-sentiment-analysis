package me.camwalford.backendapiservice.controller


import me.camwalford.backendapiservice.dto.AuthenticationRequest
import me.camwalford.backendapiservice.dto.AuthenticationResponse
import me.camwalford.backendapiservice.dto.RefreshTokenRequest
import me.camwalford.backendapiservice.dto.TokenResponse
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import me.camwalford.backendapiservice.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val refreshTokenRepository: RefreshTokenRepository

) {
    @PostMapping
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): AuthenticationResponse =
        authenticationService.authentication(authRequest)

    @PostMapping("/refresh")
    fun refreshAccessToken(
        @RequestBody request: RefreshTokenRequest
    ): TokenResponse =
        authenticationService.refreshAccessToken(request.token)
            ?.mapToTokenResponse()
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token.")

    private fun String.mapToTokenResponse(): TokenResponse =
        TokenResponse(
            token = this
        )

    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshTokenRequest): ResponseEntity<Void> {
        refreshTokenRepository.deleteByToken(request.token)
        return ResponseEntity.noContent().build()
    }
}