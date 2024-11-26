package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.config.CorsProperties
import me.camwalford.backendapiservice.controller.user.EndpointStatsResponse
import me.camwalford.backendapiservice.controller.user.UserResponse
import me.camwalford.backendapiservice.controller.user.UserStatsResponse
import me.camwalford.backendapiservice.exception.RequestException
import me.camwalford.backendapiservice.exception.RequestStatsNotFoundException
import me.camwalford.backendapiservice.exception.RequestTrackingException
import me.camwalford.backendapiservice.model.User
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import me.camwalford.backendapiservice.repository.RequestRepository
import me.camwalford.backendapiservice.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class RequestService(
        private val requestRepository: RequestRepository,
        private val userRepository: UserRepository,
) {
        private val logger = LoggerFactory.getLogger(RequestService::class.java)

        fun getEndpointStats(): List<EndpointStatsResponse> {
                logger.info("Fetching endpoint statistics")
                try {
                        val stats = requestRepository.findTotalRequestsGroupedByUriAndMethod()
                        if (stats.isEmpty()) {
                                logger.warn("No endpoint statistics found")
                                return emptyList()
                        }

                        return stats.map { (uri, method, total) ->
                                EndpointStatsResponse(
                                        method = method as String,
                                        uri = uri as String,
                                        totalRequests = (total as Number).toLong()
                                )
                        }
                } catch (ex: Exception) {
                        logger.error("Error fetching endpoint statistics", ex)
                        throw RequestTrackingException(
                                "Failed to retrieve endpoint statistics",
                                mapOf("error" to (ex.message ?: "Unknown error"))
                        )
                }
        }

        fun getAllUserStats(): List<UserStatsResponse> {
                logger.info("Fetching all user statistics")
                try {
                        val stats = requestRepository.findTotalRequestsGroupedByUser()
                        val users = userRepository.findAll().associateBy { it.id }

                        if (users.isEmpty()) {
                                logger.warn("No users found in the system")
                                return emptyList()
                        }

                        return stats.map { (userId, total) ->
                                val user = users[userId as Long] ?: throw RequestStatsNotFoundException(
                                        identifier = userId,
                                        details = mapOf(
                                                "userId" to userId,
                                                "operation" to "getAllUserStats"
                                        )
                                )

                                UserStatsResponse(
                                        userId = userId,
                                        username = user.username,
                                        email = user.email,
                                        role = user.role.toString(),
                                        credits = user.credits,
                                        totalRequests = (total as Number).toLong()
                                )
                        }
                } catch (ex: Exception) {
                        when (ex) {
                                is RequestException -> throw ex
                                else -> throw RequestTrackingException(
                                        "Failed to retrieve user statistics",
                                        mapOf("error" to (ex.message ?: "Unknown error"))
                                )
                        }
                }
        }

        fun getUserStatsById(userId: Long): UserStatsResponse {
                logger.info("Fetching user statistics for ID: $userId")
                val user = userRepository.findById(userId).orElseThrow {
                        RequestStatsNotFoundException(
                                identifier = userId,
                                details = mapOf(
                                        "userId" to userId,
                                        "operation" to "getUserStatsById"
                                )
                        )
                }
                return user.toUserStatsResponse()
        }

        fun getUserStatsByUsername(username: String): UserStatsResponse {
                logger.info("Fetching user statistics for username: $username")
                val user = userRepository.findUserByUsername(username) ?: throw RequestStatsNotFoundException(
                        identifier = username,
                        details = mapOf(
                                "username" to username,
                                "operation" to "getUserStatsByUsername"
                        )
                )
                return user.toUserStatsResponse()
        }

        private fun User.toUserStatsResponse(): UserStatsResponse {
                return try {
                        UserStatsResponse(
                                userId = this.id,
                                username = this.username,
                                email = this.email,
                                role = this.role.toString(),
                                credits = this.credits,
                                totalRequests = requestRepository.findTotalRequestsForUser(this.id).orElse(0L)
                        )
                } catch (ex: Exception) {
                        logger.error("Error creating UserStatsResponse for user: ${this.username}", ex)
                        throw RequestTrackingException(
                                "Failed to create user statistics response",
                                mapOf(
                                        "userId" to this.id,
                                        "username" to this.username,
                                        "error" to (ex.message ?: "Unknown error")
                                )
                        )
                }
        }
}