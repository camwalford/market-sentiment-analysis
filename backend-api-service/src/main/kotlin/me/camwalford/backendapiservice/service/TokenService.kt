package me.camwalford.backendapiservice.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import me.camwalford.backendapiservice.config.JwtProperties
import me.camwalford.backendapiservice.exception.InvalidTokenException
import me.camwalford.backendapiservice.exception.TokenValidationException
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date


@Service
class TokenService(
    jwtProperties: JwtProperties
) {
    private val logger = LoggerFactory.getLogger(TokenService::class.java)
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    fun generate(
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String {
        try {
            return Jwts.builder()
                .claims()
                .subject(userDetails.username)
                .issuedAt(Date(System.currentTimeMillis()))
                .expiration(expirationDate)
                .add(additionalClaims)
                .and()
                .signWith(secretKey)
                .compact()
        } catch (ex: Exception) {
            logger.error("Error generating token for user: ${userDetails.username}", ex)
            throw InvalidTokenException(
                "Failed to generate token",
                mapOf(
                    "username" to userDetails.username,
                    "error" to (ex.message ?: "Unknown error")
                )
            )
        }
    }

    fun isValid(token: String, userDetails: UserDetails): Boolean {
        try {
            val username = extractUsername(token)
            return username == userDetails.username && !isExpired(token)
        } catch (ex: Exception) {
            logger.error("Error validating token", ex)
            throw TokenValidationException(
                "Invalid token",
                mapOf(
                    "error" to (ex.message ?: "Unknown error"),
                    "username" to userDetails.username
                )
            )
        }
    }

    fun extractUsername(token: String): String {
        try {
            return getAllClaims(token).subject
        } catch (ex: Exception) {
            logger.error("Error extracting username from token", ex)
            throw TokenValidationException(
                "Failed to extract username from token",
                mapOf("error" to (ex.message ?: "Unknown error"))
            )
        }
    }

    fun isExpired(token: String): Boolean {
        try {
            val expiration = getAllClaims(token).expiration
            return expiration.before(Date(System.currentTimeMillis()))
        } catch (ex: Exception) {
            logger.error("Error checking token expiration", ex)
            throw TokenValidationException(
                "Failed to check token expiration",
                mapOf("error" to (ex.message ?: "Unknown error"))
            )
        }
    }

    private fun getAllClaims(token: String): Claims {
        try {
            val parser = Jwts.parser()
                .verifyWith(secretKey)
                .build()

            return parser
                .parseSignedClaims(token)
                .payload
        } catch (ex: Exception) {
            logger.error("Error parsing token claims", ex)
            throw TokenValidationException(
                "Failed to parse token",
                mapOf("error" to (ex.message ?: "Unknown error"))
            )
        }
    }
}