package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.config.JwtProperties
import me.camwalford.backendapiservice.dto.AuthenticationRequest
import me.camwalford.backendapiservice.dto.AuthenticationResponse
import me.camwalford.backendapiservice.model.RefreshToken
import me.camwalford.backendapiservice.model.User
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val logger: Logger = LoggerFactory.getLogger(AuthenticationService::class.java)

    fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        logger.info("Authenticating user with email: ${authenticationRequest.email}")
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

        logger.info("User authenticated and tokens generated for email: ${authenticationRequest.email}")
        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun refreshAccessToken(refreshToken: String): String? {
        logger.info("Refreshing access token using refresh token: $refreshToken")
        val refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken) ?: run {
            logger.warn("Invalid refresh token: $refreshToken")
            return null
        }
        val user = refreshTokenEntity.user
        val userDetails = userDetailsService.loadUserByUsername(user.email)
        return if (!tokenService.isExpired(refreshToken)) {
            logger.info("Refresh token is valid, generating new access token for user: ${user.email}")
            createAccessToken(userDetails)
        } else {
            logger.warn("Refresh token is expired: $refreshToken")
            null
        }
    }

    private fun createAccessToken(user: UserDetails): String {
        logger.debug("Creating access token for user: ${user.username}")
        return tokenService.generate(
            userDetails = user,
            expirationDate = getAccessTokenExpiration()
        )
    }

    private fun createRefreshToken(user: UserDetails): String {
        logger.debug("Creating refresh token for user: ${user.username}")
        return tokenService.generate(
            userDetails = user,
            expirationDate = getRefreshTokenExpiration()
        )
    }

    private fun getAccessTokenExpiration(): Date {
        val expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        logger.debug("Access token expiration date: $expirationDate")
        return expirationDate
    }

    private fun getRefreshTokenExpiration(): Date {
        val expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
        logger.debug("Refresh token expiration date: $expirationDate")
        return expirationDate
    }
}