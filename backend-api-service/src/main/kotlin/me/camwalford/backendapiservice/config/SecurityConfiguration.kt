package me.camwalford.backendapiservice.config

import jakarta.servlet.DispatcherType.ERROR
import jakarta.servlet.DispatcherType.FORWARD
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider
) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain =
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                    // Swagger UI and OpenAPI endpoints
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html/*",
                        "/webjars/**",
                        "/error"
                    ).permitAll()
                    // Auth endpoints
                    .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/error",
                        "/api/auth/logout",
                        "/api/auth/register",
                        "/api/auth/validate",
                        "/api/auth/check-username/**",
                        "/api/auth/check-email/**"
                    ).permitAll()
                    // Protected endpoints
                    .requestMatchers(HttpMethod.POST, "/api/sentiment")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/user/me")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/api/user/**")
                    .hasRole("ADMIN")
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(
            "http://localhost:3000",
            "http://web-dashboard-service:3000",
            "https://stocks.camwalford.me"
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf(
            "Content-Type",
            "Authorization",
            "X-Requested-With",
            "Accept",
            "Origin"
        )
        configuration.allowCredentials = true
        configuration.exposedHeaders = listOf("Set-Cookie")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
