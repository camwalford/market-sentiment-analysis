package me.camwalford.backendapiservice.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.camwalford.backendapiservice.service.CustomUserDetailsService
import me.camwalford.backendapiservice.service.TokenService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info("Processing request to: ${request.requestURI}")
        if (request.requestURI.startsWith("/api/auth/") || request.requestURI == "/error") {
            logger.info("Skipping JWT authentication for auth endpoint: ${request.requestURI}")
            filterChain.doFilter(request, response)
            return
        }

        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader.doesNotContainBearerToken()) {
            logger.info("Authorization header missing or does not contain Bearer token.")
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractTokenValue()
        val email = tokenService.extractEmail(jwtToken)
        val username = tokenService.extractUsername(jwtToken)

        if (username == null) {
            logger.info("JWT does not contain a valid email.")
        } else {
            logger.info("Extracted email from JWT: $username")
        }
        if (SecurityContextHolder.getContext().authentication == null){
            logger.info("Security context has no authentication.")
        } else {
            logger.info("Security context already contains authentication.")
        }
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            logger.info("Security context has no authentication. Loading user details for: $username")
            val foundUser = userDetailsService.loadUserByUsername(username)

            if (tokenService.isValid(jwtToken, foundUser)) {
                logger.info("JWT token is valid for user: ${foundUser.username}. Updating security context.")
                updateContext(foundUser, request)
            } else {
                logger.warn("JWT token is invalid or expired for user: $email.")
            }
        } else if (SecurityContextHolder.getContext().authentication != null) {
            logger.info("Security context already contains authentication. Skipping update.")
        }

        filterChain.doFilter(request, response)
    }

    private fun String?.doesNotContainBearerToken() =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
        this.substringAfter("Bearer ")

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
        logger.info("Updated SecurityContext with authentication for user: ${foundUser.username} with authorities: ${foundUser.authorities}")
    }
}
