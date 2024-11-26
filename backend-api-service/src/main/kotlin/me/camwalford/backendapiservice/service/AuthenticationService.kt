package me.camwalford.backendapiservice.service

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.camwalford.backendapiservice.config.JwtProperties
import me.camwalford.backendapiservice.controller.auth.AuthenticationRequest
import me.camwalford.backendapiservice.controller.auth.AuthenticationResponse
import me.camwalford.backendapiservice.controller.user.UserResponse
import me.camwalford.backendapiservice.model.RefreshToken
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import me.camwalford.backendapiservice.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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
    private val userRepository: UserRepository,
    private val response: HttpServletResponse
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthenticationService::class.java)

    @Value("\${app.cookie.domain}")
    private lateinit var cookieDomain: String

    @Value("\${app.cookie.secure}")
    private var cookieSecure: Boolean = true

    @Value("\${app.cookie.same-site}")
    private lateinit var cookieSameSite: String

    fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        logger.info("Authenticating user with username: ${authenticationRequest.username}")
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.username,
                authenticationRequest.password
            )
        )

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

        // Set cookies
        addAccessTokenCookie(accessToken)
        addRefreshTokenCookie(refreshToken)

        logger.info("User authenticated and tokens generated for username: ${authenticationRequest.username}")
        return AuthenticationResponse(
            user = UserResponse.toResponse(applicationUser)
        )
    }

    fun refreshAccessToken(refreshToken: String): String? {
        val refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken) ?: return null
        val userDetails = userDetailsService.loadUserByUsername(refreshTokenEntity.user.username)

        return if (!tokenService.isExpired(refreshToken)) {
            val newAccessToken = createAccessToken(userDetails)
            addAccessTokenCookie(newAccessToken)
            newAccessToken
        } else {
            null
        }
    }

    private fun addAccessTokenCookie(token: String) {
        createCookie("access_token", token, (jwtProperties.accessTokenExpiration / 1000).toInt())
            .let { response.addCookie(it) }
    }

    private fun addRefreshTokenCookie(token: String) {
        createCookie("refresh_token", token, (jwtProperties.refreshTokenExpiration / 1000).toInt())
            .let { response.addCookie(it) }
    }

    private fun createCookie(name: String, value: String, maxAge: Int): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = true
            secure = cookieSecure
            path = "/"
            this.maxAge = maxAge
            domain = cookieDomain
            setAttribute("SameSite", cookieSameSite)
        }
    }

    fun logout(request: HttpServletRequest) {
        // Clear cookies
        createCookie("access_token", "", 0).let { response.addCookie(it) }
        createCookie("refresh_token", "", 0).let { response.addCookie(it) }

        // Get refresh token and invalidate it
        request.cookies?.find { it.name == "refresh_token" }?.value?.let { token ->
            refreshTokenRepository.deleteByToken(token)
        }
    }

//
//    private fun getCookieValue(request: HttpServletRequest, name: String): String? {
//        return request.cookies?.find { it.name == name }?.value
//    }

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
        logger.debug("Access token expiration date: {}", expirationDate)
        return expirationDate
    }

    private fun getRefreshTokenExpiration(): Date {
        val expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
        logger.debug("Refresh token expiration date: {}", expirationDate)
        return expirationDate
    }
}