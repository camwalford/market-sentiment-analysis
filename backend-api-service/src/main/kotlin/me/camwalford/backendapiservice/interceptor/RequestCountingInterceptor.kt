package me.camwalford.backendapiservice.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import me.camwalford.backendapiservice.exception.RequestTrackingException
import me.camwalford.backendapiservice.exception.UserNotFoundException
import me.camwalford.backendapiservice.model.Request
import me.camwalford.backendapiservice.model.User
import me.camwalford.backendapiservice.repository.RequestRepository
import me.camwalford.backendapiservice.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RequestCountingInterceptor(
    private val requestRepository: RequestRepository,
    private val userRepository: UserRepository
) : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(RequestCountingInterceptor::class.java)

    @Transactional
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        try {
            logger.debug("Processing request to: ${request.requestURI}")

            val authentication = SecurityContextHolder.getContext().authentication
            if (!isValidAuthentication(authentication)) {
                logger.debug("Skipping request tracking - no valid authentication")
                return
            }

            val username = (authentication.principal as UserDetails).username
            val user = userRepository.findUserByUsername(username) ?: throw UserNotFoundException(
                identifier = username,
                details = mapOf(
                    "uri" to request.requestURI,
                    "method" to request.method
                )
            )

            trackRequest(user, request.requestURI, request.method)

        } catch (ex: Exception) {
            logger.error("Error tracking request", ex)
        }
    }

    private fun isValidAuthentication(authentication: Authentication?): Boolean {
        return authentication != null &&
                authentication.isAuthenticated &&
                authentication.principal is UserDetails
    }

    private fun trackRequest(user: User, uri: String, method: String) {
        try {
            logger.debug("Tracking request for user: ${user.username}, URI: $uri, Method: $method")

            val existingRequest = requestRepository.findByUserIdAndUriAndMethod(user.id, uri, method)

            if (existingRequest != null) {
                existingRequest.total += 1
                requestRepository.save(existingRequest)
                logger.debug("Updated existing request count: ${existingRequest.total}")
            } else {
                val newRequest = Request(
                    user = user,
                    uri = uri,
                    method = method,
                    total = 1
                )
                requestRepository.save(newRequest)
                logger.debug("Created new request tracking entry")
            }
        } catch (ex: Exception) {
            logger.error("Failed to track request", ex)
            throw RequestTrackingException(
                "Failed to track request",
                mapOf(
                    "userId" to user.id,
                    "username" to user.username,
                    "uri" to uri,
                    "method" to method,
                    "error" to (ex.message ?: "Unknown error")
                )
            )
        }
    }
}


