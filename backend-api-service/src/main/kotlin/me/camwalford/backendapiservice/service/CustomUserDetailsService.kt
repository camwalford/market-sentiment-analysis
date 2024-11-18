package me.camwalford.backendapiservice.service

import me.camwalford.backendapiservice.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias ApplicationUser = me.camwalford.backendapiservice.model.User

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    private val logger: Logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        logger.info("Loading user by username: $username")
        val user = userRepository.findUserByUsername(username)
            ?.mapToUserDetails()
            ?: run {
                logger.warn("User not found with username: $username")
                throw UsernameNotFoundException("User not found")
            }
        logger.info(
            "Loaded user details: username={}, password={}, ={}",
            user.username,
            user.password,
            user.authorities
        )
        return user
    }

    private fun ApplicationUser.mapToUserDetails(): UserDetails {
        logger.debug("Mapping ApplicationUser to UserDetails for user: ${this.username}")
        return User.builder()
            .username(this.username)
            .password(this.password)
            .roles(this.role.name)
            .build()
    }
}