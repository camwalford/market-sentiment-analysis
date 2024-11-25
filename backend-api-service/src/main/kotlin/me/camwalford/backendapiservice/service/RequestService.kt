package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.config.CorsProperties
import me.camwalford.backendapiservice.controller.user.EndpointStatsResponse
import me.camwalford.backendapiservice.controller.user.UserResponse
import me.camwalford.backendapiservice.controller.user.UserStatsResponse
import me.camwalford.backendapiservice.repository.RefreshTokenRepository
import me.camwalford.backendapiservice.repository.RequestRepository
import me.camwalford.backendapiservice.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class RequestService(
        private val requestRepository: RequestRepository,
        private val userRepository: UserRepository,
) {
        fun getEndpointStats(): List<EndpointStatsResponse> {
                val stats = requestRepository.findTotalRequestsGroupedByUriAndMethod()
                return stats.map { (uri, method, total) ->
                        EndpointStatsResponse(
                                method = method as String,
                                uri = uri as String,
                                totalRequests = (total as Number).toLong()
                        )
                }
        }


        fun getAllUserStats(): List<UserStatsResponse> {
                val stats = requestRepository.findTotalRequestsGroupedByUser()
                val users = userRepository.findAll().associateBy { it.id }
                return stats.map { (userId, total) ->
                        val user = users[userId as Long]
                        UserStatsResponse(
                                userId = userId,
                                username = user?.username ?: "Unknown",
                                email = user?.email ?: "Unknown",
                                role = user?.role.toString() ?: "Unknown",
                                credits = user?.credits ?: 0,
                                totalRequests = (total as Number).toLong()
                        )
                }
        }

        fun getUserStats(userId: Long): UserStatsResponse? {
                val user = userRepository.findById(userId).orElse(null) ?: return null
                val totalRequests = requestRepository.findTotalRequestsForUser(userId).orElse(0L)

                return UserStatsResponse(
                        userId = userId,
                        username = user.username,
                        email = user.email,
                        role = user.role.toString(),
                        credits = user.credits,
                        totalRequests = totalRequests
                )
        }

        fun getUserStatsByUsername(username: String): UserStatsResponse? {
                val user = userRepository.findUserByUsername(username) ?: return null
                val userId = user.id
                val totalRequests = requestRepository.findTotalRequestsForUser(userId).orElse(0L)

                return UserStatsResponse(
                        userId = userId,
                        username = user.username,
                        email = user.email,
                        role = user.role.toString(),
                        credits = user.credits,
                        totalRequests = totalRequests
                )
        }





}