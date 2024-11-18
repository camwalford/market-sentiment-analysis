package me.camwalford.backendapiservice.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration





@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "bearerAuth"
        return OpenAPI()
            .info(
                Info()
                    .title("Sentiment Analysis API")
                    .description("""
                        API for analyzing sentiment of financial news.
                        
                        How to authorize:
                        1. Login using /api/auth/login endpoint
                        2. Copy the JWT token from the response
                        3. Click 'Authorize' button at the top
                        4. Enter the token in the format: Bearer <your-token>
                    """.trimIndent())
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Cameron Walford")
                            .email("cwalfordadmin@mailinator.com")
                    )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Enter your JWT token in the format: Bearer <token>")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
    }
}