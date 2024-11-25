package me.camwalford.backendapiservice.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import me.camwalford.backendapiservice.model.Request
import me.camwalford.backendapiservice.repository.RequestRepository
import me.camwalford.backendapiservice.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RequestCountingInterceptor(
    private val requestRepository: RequestRepository,
    private val userRepository: UserRepository
) : HandlerInterceptor{

    private val logger = LoggerFactory.getLogger(RequestCountingInterceptor::class.java)

    @Transactional
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        logger.info("Processing request to: ${request.requestURI}")
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal
            if (principal is UserDetails) {
                val username = principal.username
                val user = userRepository.findUserByUsername(username)
                logger.info("User found: $user")
                if (user != null) {
                    val uri = request.requestURI
                    val method = request.method
                    logger.info("Request URI: $uri")
                    logger.info("Request Method: $method")
                    // Check if a Request record exists for this user, uri, and method


                    val existingRequest = requestRepository.findByUserIdAndUriAndMethod(user.id, uri, method)

                    if (existingRequest != null) {
                        existingRequest.total += 1
                        requestRepository.save(existingRequest)
                    } else {
                        val newRequest = Request(
                            user = user,
                            uri = uri,
                            method = method,
                            total = 1
                        )
                        requestRepository.save(newRequest)
                    }
                }
            }
        }
    }
}


