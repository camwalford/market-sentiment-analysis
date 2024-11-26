package me.camwalford.backendapiservice.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.examples.Example
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ProblemDetail

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "bearerAuth"

        // Create Problem Detail Schema
        val problemDetailSchema = Schema<ProblemDetail>()
            .type("object")
            .description("RFC 7807 Problem Details")
            .with {
                addProperty("type", Schema<String>()
                    .type("string")
                    .description("A URI reference that identifies the problem type (YET TO BE IMPLEMENTED)")
                    .example("https://api.yourservice.com/errors/AUTH_001"))

                addProperty("title", Schema<String>()
                    .type("string")
                    .description("A short, human-readable summary of the problem type")
                    .example("Authentication Failed"))

                addProperty("status", Schema<Int>()
                    .type("integer")
                    .description("The HTTP status code")
                    .example(401))

                addProperty("detail", Schema<String>()
                    .type("string")
                    .description("A human-readable explanation specific to this occurrence of the problem")
                    .example("Invalid credentials provided"))

                addProperty("instance", Schema<String>()
                    .type("string")
                    .description("A URI reference that identifies the specific occurrence of the problem")
                    .example("/api/auth/login"))

                addProperty("timestamp", Schema<String>()
                    .type("string")
                    .format("date-time")
                    .description("The time when the error occurred")
                    .example("2024-01-01T00:00:00Z"))

                addProperty("errorCode", Schema<String>()
                    .type("string")
                    .description("A specific error code for this type of error")
                    .example("AUTH_001"))
            }

        // Create example responses
        val unauthorizedExample = Example().apply {
            summary = "Unauthorized Error"
            description = "Example of an authentication error response"
            value = mapOf(
                "type" to "https://api.yourservice.com/errors/AUTH_001",
                "title" to "Authentication Failed",
                "status" to 401,
                "detail" to "Invalid credentials provided",
                "instance" to "/api/auth/login",
                "timestamp" to "2024-01-01T00:00:00Z",
                "errorCode" to "AUTH_001"
            )
        }

        val validationExample = Example().apply {
            summary = "Validation Error"
            description = "Example of a validation error response"
            value = mapOf(
                "type" to "https://api.yourservice.com/errors/VALIDATION_001",
                "title" to "Validation Error",
                "status" to 400,
                "detail" to "Invalid input parameters",
                "instance" to "/api/sentiment/analyze",
                "timestamp" to "2024-01-01T00:00:00Z",
                "errorCode" to "VALIDATION_001",
                "fields" to mapOf(
                    "ticker" to "Must not be blank",
                    "fromDate" to "Must be a valid date"
                )
            )
        }

        return OpenAPI()
            .info(
                Info()
                    .title("Sentiment Analysis API")
                    .description(
                        """
                        API for analyzing sentiment of financial news.
                        
                        How to authorize:
                        1. Login using /api/auth/login endpoint
                        2. Copy the JWT token from the response
                        3. Click 'Authorize' button at the top
                        4. Enter the token in the format: Bearer <your-token>
                        
                        Error Responses:
                        All error responses follow the RFC 7807 Problem Details format.
                        
                        Common error codes:
                        Authentication Errors:
                        - AUTH_001: Invalid credentials
                        - AUTH_002: Token validation failed
                        - AUTH_003: Token expired
                        - AUTH_004: Invalid token
                        - AUTH_005: Refresh token not found
                        
                        Request Errors:
                        - REQUEST_001: Request tracking error
                        - REQUEST_002: Request statistics not found
                        - REQUEST_003: Invalid request statistics
                        
                        Validation Errors:
                        - VALIDATION_001: Request validation failed
                        
                        User Errors:
                        - USER_001: User not found
                        - USER_002: Duplicate user
                        - USER_003: Insufficient credits
                        - USER_004: User banned
                        - USER_005: Invalid operation
                        
                        Service Errors:
                        - SENTIMENT_001: Sentiment analysis service error
                        - NEWS_001: News service error
                        """.trimIndent()
                    )
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Cameron Walford")
                            .email("cwalfordadmin@mailinator.com")
                    )
            )
            .addServersItem(
                Server()
                    .url("https://api.camwalford.me")
                    .description("Production server")
            )
            .addServersItem(
                Server()
                    .url("http://localhost:8080")
                    .description("Local development server")
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
                    .addSchemas("ProblemDetail", problemDetailSchema)
                    .addResponses(
                        "UnauthorizedError",
                        ApiResponse()
                            .description("Authentication information is missing or invalid")
                            .content(
                                Content().addMediaType(
                                    "application/problem+json",
                                    MediaType()
                                        .schema(Schema<ProblemDetail>().`$ref`("#/components/schemas/ProblemDetail"))
                                        .examples(mapOf("unauthorizedError" to unauthorizedExample))
                                )
                            )
                    )
                    .addResponses(
                        "ValidationError",
                        ApiResponse()
                            .description("Invalid input parameters")
                            .content(
                                Content().addMediaType(
                                    "application/problem+json",
                                    MediaType()
                                        .schema(Schema<ProblemDetail>().`$ref`("#/components/schemas/ProblemDetail"))
                                        .examples(mapOf("validationError" to validationExample))
                                )
                            )
                    )

            )
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
    }
}

// Extension function to make the builder pattern more readable
private fun <T> T.with(block: T.() -> Unit): T = apply(block)