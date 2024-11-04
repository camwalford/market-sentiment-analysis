package me.camwalford.backendapiservice.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for the application. Enables JWT configuration properties to be loaded from the application.yml.
 * Reference: https://codersee.com/spring-boot-3-spring-security-6-with-kotlin-jwt/
 */

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class Configuration