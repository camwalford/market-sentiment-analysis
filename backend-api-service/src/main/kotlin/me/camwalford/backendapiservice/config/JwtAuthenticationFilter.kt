package me.camwalford.backendapiservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.camwalford.backendapiservice.exception.AuthException
import me.camwalford.backendapiservice.exception.InvalidCredentialsException
import me.camwalford.backendapiservice.exception.InvalidTokenException
import me.camwalford.backendapiservice.exception.RefreshTokenNotFoundException
import me.camwalford.backendapiservice.exception.TokenExpiredException
import me.camwalford.backendapiservice.exception.TokenValidationException
import me.camwalford.backendapiservice.service.CustomUserDetailsService
import me.camwalford.backendapiservice.service.TokenService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.net.URI
import java.time.Instant

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info("Starting JWT authentication filter for request: ${request.requestURI}")
        try {
            val jwtToken = extractTokenFromCookie(request)
            if (jwtToken != null) {
                logger.debug("Token extracted from cookie: $jwtToken")
                processToken(jwtToken, request)
            } else {
                logger.debug("No token found in cookies for request: ${request.requestURI}")
            }
            filterChain.doFilter(request, response)
        } catch (ex: AuthException) {
            logger.error("Authentication exception occurred: ${ex.message}", ex)
            handleAuthError(ex, response)
        } catch (ex: Exception) {
            logger.error("Unexpected exception occurred during authentication: ${ex.message}", ex)
            handleUnexpectedError(ex, response)
        }
    }

    private fun handleAuthError(ex: AuthException, response: HttpServletResponse) {
        logger.debug("Handling authentication error: ${ex.message}")
        val problemDetail = ProblemDetail
            .forStatusAndDetail(ex.statusCode, ex.message ?: "Authentication failed")
            .apply {
                title = when (ex) {
                    is InvalidCredentialsException -> "Invalid Credentials"
                    is TokenExpiredException -> "Token Expired"
                    is TokenValidationException -> "Token Validation Failed"
                    is InvalidTokenException -> "Invalid Token"
                    is RefreshTokenNotFoundException -> "Refresh Token Not Found"
                }
                type = URI.create("https://api.yourservice.com/errors/${ex.errorCode}")
                setProperty("timestamp", ex.timestamp.toString())
                setProperty("errorCode", ex.errorCode)
                ex.details?.forEach { (key, value) -> setProperty(key, value) }
            }

        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.status = ex.statusCode.value()
        response.writer.write(objectMapper.writeValueAsString(problemDetail))
    }

    private fun handleUnexpectedError(ex: Exception, response: HttpServletResponse) {
        logger.debug("Handling unexpected error: ${ex.message}")
        val problemDetail = ProblemDetail
            .forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred during authentication"
            ).apply {
                title = "Authentication Error"
                type = URI.create("https://api.yourservice.com/errors/AUTH_999")
                setProperty("timestamp", Instant.now().toString())
                setProperty("errorCode", "AUTH_999")
                setProperty("error", ex.message ?: "Unknown error")
            }

        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        response.writer.write(objectMapper.writeValueAsString(problemDetail))
    }

    private fun processToken(token: String, request: HttpServletRequest) {
        logger.debug("Processing token for authentication: $token")
        try {
            val username = tokenService.extractUsername(token)
            if (username != null) {
                logger.debug("Extracted username from token: $username")
                if (SecurityContextHolder.getContext().authentication == null) {
                    logger.debug("No existing authentication found in SecurityContext")
                    val userDetails = userDetailsService.loadUserByUsername(username)
                    if (tokenService.isValid(token, userDetails)) {
                        logger.debug("Token is valid for user: $username")
                        updateContext(userDetails, request)
                    } else {
                        logger.warn("Token is invalid or expired for user: $username")
                        throw TokenExpiredException("Access", mapOf("username" to username))
                    }
                } else {
                    logger.debug("Authentication already present in SecurityContext for user: $username")
                }
            } else {
                logger.warn("Unable to extract username from token: $token")
                throw InvalidTokenException("Invalid token structure", mapOf("token" to token))
            }
        } catch (ex: UsernameNotFoundException) {
            logger.error("User not found for token: $token", ex)
            throw InvalidTokenException("User not found", mapOf("token" to token))
        }
    }

    private fun extractTokenFromCookie(request: HttpServletRequest): String? {
        val cookies = request.cookies
        if (cookies == null) {
            logger.debug("No cookies found in the request")
            return null
        }
        val tokenCookie = cookies.find { it.name == "access_token" }
        return tokenCookie?.value?.also { logger.debug("Found access token in cookie: $it") }
    }

    private fun updateContext(userDetails: UserDetails, request: HttpServletRequest) {
        logger.debug("Updating SecurityContext for user: ${userDetails.username}")
        val authToken = UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        )
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
        logger.debug("SecurityContext updated with authentication for user: ${userDetails.username}")
    }
}
