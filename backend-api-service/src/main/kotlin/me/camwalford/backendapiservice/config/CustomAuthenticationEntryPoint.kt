package me.camwalford.backendapiservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import java.net.URI
import java.time.Instant

class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val problemDetail = ProblemDetail
            .forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                authException.message ?: "Authentication required"
            ).apply {
                title = "Authentication Required"
                type = URI.create("https://api.yourservice.com/errors/AUTH_001")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", Instant.now().toString())
                setProperty("errorCode", "AUTH_001")
            }

        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.write(objectMapper.writeValueAsString(problemDetail))
    }
}

class CustomAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val problemDetail = ProblemDetail
            .forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                accessDeniedException.message ?: "Access denied"
            ).apply {
                title = "Access Denied"
                type = URI.create("https://api.yourservice.com/errors/AUTH_003")
                instance = URI.create(request.requestURI)
                setProperty("timestamp", Instant.now().toString())
                setProperty("errorCode", "AUTH_003")
            }

        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.status = HttpStatus.FORBIDDEN.value()
        response.writer.write(objectMapper.writeValueAsString(problemDetail))
    }
}