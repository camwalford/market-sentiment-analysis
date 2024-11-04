package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.config.JwtProperties
import me.camwalford.backendapiservice.dto.AuthenticationRequest
import me.camwalford.backendapiservice.dto.AuthenticationResponse
import me.camwalford.backendapiservice.model.RefreshToken
import me.camwalford.backendapiservice.model.User
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.email,
                authenticationRequest.password
            )
        )
        val user = userDetailsService.loadUserByUsername(authenticationRequest.email)
        val accessToken = createAccessToken(user)
        val refreshToken = createRefreshToken(user)

        // Save refresh token in the database
        val refreshTokenEntity = RefreshToken(
            token = refreshToken,
            user = user as User
        )
        refreshTokenRepository.save(refreshTokenEntity)

        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun refreshAccessToken(refreshToken: String): String? {
        val refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken) ?: return null
        val user = refreshTokenEntity.user
        val userDetails = userDetailsService.loadUserByUsername(user.email)
        return if (!tokenService.isExpired(refreshToken)) {
            createAccessToken(userDetails)
        } else {
            null
        }
    }

    private fun createAccessToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = getAccessTokenExpiration()
    )
    private fun createRefreshToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = getRefreshTokenExpiration()
    )
    private fun getAccessTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
    private fun getRefreshTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
}