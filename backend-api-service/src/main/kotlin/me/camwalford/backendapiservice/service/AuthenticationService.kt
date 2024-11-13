package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.config.JwtProperties
import me.camwalford.backendapiservice.controller.auth.AuthenticationRequest
import me.camwalford.backendapiservice.controller.auth.AuthenticationResponse
import me.camwalford.backendapiservice.controller.user.UserResponse
import me.camwalford.backendapiservice.model.RefreshToken
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import me.camwalford.backendapiservice.repository.UserRepository
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
    private val userRepository: UserRepository // Add UserRepository to load ApplicationUser directly
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthenticationService::class.java)

    fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        logger.info("Authenticating user with username: ${authenticationRequest.username}")
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.username,
                authenticationRequest.password
            )
        )

        // Load UserDetails and ApplicationUser
        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
        val applicationUser = userRepository.findUserByUsername(authenticationRequest.username)
            ?: throw IllegalStateException("User not found in database")

        val accessToken = createAccessToken(userDetails)
        val refreshToken = createRefreshToken(userDetails)

        // Save refresh token with ApplicationUser
        val refreshTokenEntity = RefreshToken(
            token = refreshToken,
            user = applicationUser
        )
        refreshTokenRepository.save(refreshTokenEntity)

        // Create UserResponse
        val userResponse = UserResponse.toResponse(applicationUser)

        logger.info("User authenticated and tokens generated for username: ${authenticationRequest.username}")
        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = userResponse
        )
    }

    fun refreshAccessToken(refreshToken: String): String? {
        logger.info("Refreshing access token using refresh token: $refreshToken")
        val refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken) ?: run {
            logger.warn("Invalid refresh token: $refreshToken")
            return null
        }

        // Load UserDetails for token generation
        val userDetails = userDetailsService.loadUserByUsername(refreshTokenEntity.user.username)

        return if (!tokenService.isExpired(refreshToken)) {
            logger.info("Refresh token is valid, generating new access token for user: ${refreshTokenEntity.user.username}")
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
