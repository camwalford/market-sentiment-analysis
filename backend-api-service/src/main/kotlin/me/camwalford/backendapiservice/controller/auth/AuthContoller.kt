package me.camwalford.backendapiservice.controller.auth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory
import me.camwalford.backendapiservice.controller.user.UserRequest
import me.camwalford.backendapiservice.controller.user.UserResponse
import me.camwalford.backendapiservice.service.AuthenticationService
import me.camwalford.backendapiservice.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization operations")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val userService: UserService,
    private val request: HttpServletRequest
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/register")
    @Operation(
        summary = "Register new user",
        description = "Creates a new user account in the system. No authentication required."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully registered user",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = UserResponse::class)
            )]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            ref = "#/components/responses/ValidationError"
        ),
        ApiResponse(
            responseCode = "409",
            description = "Username or email already exists",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "duplicateUser",
                        summary = "Duplicate user error",
                        description = "Example of error when username/email already exists",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/USER_002",
                            "title": "Duplicate User",
                            "status": 409,
                            "detail": "User already exists with this email",
                            "errorCode": "USER_002"
                        }
                    """
                    )
                ]
            )]
        )
    ])
    fun register(@RequestBody userRequest: UserRequest): UserResponse {
        logger.info("Registering new user with email: ${userRequest.email}")
        return userService.createUser(userRequest.toModel()).let { UserResponse.toResponse(it) }
    }

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticates user and returns tokens in HTTP-only cookies"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = [Content(schema = Schema(implementation = AuthenticationResponse::class))]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Authentication failed",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "invalidCredentials",
                        summary = "Invalid credentials",
                        description = "Example of authentication failure",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/AUTH_001",
                            "title": "Authentication Failed",
                            "status": 401,
                            "detail": "Invalid username or password",
                            "errorCode": "AUTH_001"
                        }
                    """
                    ),
                    ExampleObject(
                        name = "userBanned",
                        summary = "Banned user",
                        description = "Example of banned user trying to login",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/USER_004",
                            "title": "User Banned",
                            "status": 403,
                            "detail": "Your account has been banned",
                            "errorCode": "USER_004"
                        }
                    """
                    )
                ]
            )]
        )
    ])
    fun login(@RequestBody request: AuthenticationRequest): AuthenticationResponse {
        logger.info("Login attempt for username: ${request.username}")
        return authenticationService.authentication(request)
    }


    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh token",
        description = "Generates new access token using refresh token from cookie"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "New access token generated",
            content = [Content(schema = Schema(implementation = TokenResponse::class))]
        ),
        ApiResponse(
            responseCode = "403",
            description = "Invalid or missing refresh token",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "missingToken",
                        summary = "Missing refresh token",
                        description = "Refresh token not found in cookies",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/AUTH_005",
                            "title": "Refresh Token Not Found",
                            "status": 403,
                            "detail": "Refresh token not found in request",
                            "errorCode": "AUTH_005"
                        }
                    """
                    ),
                    ExampleObject(
                        name = "invalidToken",
                        summary = "Invalid refresh token",
                        description = "Refresh token is invalid or expired",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/AUTH_004",
                            "title": "Invalid Token",
                            "status": 403,
                            "detail": "Refresh token is invalid or expired",
                            "errorCode": "AUTH_004"
                        }
                    """
                    )
                ]
            )]
        )
    ])
    fun refreshToken(): TokenResponse {
        val refreshToken = request.cookies?.find { it.name == "refresh_token" }?.value
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token not found")

        return authenticationService.refreshAccessToken(refreshToken)
            ?.let { TokenResponse(it) }
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token")
    }

    @GetMapping("/check-username/{username}")
    @Operation(
        summary = "Check username availability",
        description = "Checks if a username is available for registration. No authentication required."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Username availability check completed",
            content = [Content(schema = Schema(implementation = Boolean::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid username format",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "invalidUsername",
                        summary = "Invalid username format",
                        description = "Example of error when username format is invalid",
                        value = """
                    {
                        "type": "https://api.yourservice.com/errors/USER_005",
                        "title": "Invalid Operation",
                        "status": 400,
                        "detail": "Username cannot be blank",
                        "errorCode": "USER_005"
                    }
                    """
                    )
                ]
            )]
        )
    ])
    fun checkUsername(@PathVariable username: String): Boolean {
        logger.info("Checking username availability: $username")
        return userService.isUsernameAvailable(username)
    }

    @GetMapping("/check-email/{email}")
    @Operation(
        summary = "Check email availability",
        description = "Checks if an email is available for registration. No authentication required."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Email availability check completed",
            content = [Content(schema = Schema(implementation = Boolean::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid email format",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "invalidEmail",
                        summary = "Invalid email format",
                        description = "Example of error when email format is invalid",
                        value = """
                    {
                        "type": "https://api.yourservice.com/errors/USER_005",
                        "title": "Invalid Operation",
                        "status": 400,
                        "detail": "Email cannot be blank",
                        "errorCode": "USER_005"
                    }
                    """
                    )
                ]
            )]
        )
    ])
    fun checkEmail(@PathVariable email: String): Boolean {
        logger.info("Checking email availability: $email")
        return userService.isEmailAvailable(email)
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Logout",
        description = "Invalidates tokens and clears authentication cookies. Requires authentication."
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "Successfully logged out"
        ),
        ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            ref = "#/components/responses/UnauthorizedError"
        )
    ])
    fun logout(): ResponseEntity<Void> {
        authenticationService.logout(request)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/validate")
    @Operation(
        summary = "Validate session",
        description = "Validates the current session and returns user data"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Session is valid",
            content = [Content(schema = Schema(implementation = UserResponse::class))]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Session is invalid or expired",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "invalidSession",
                        summary = "Invalid session",
                        description = "Example of error when session is invalid",
                        value = """
                    {
                        "type": "https://api.yourservice.com/errors/AUTH_002",
                        "title": "Token Validation Failed",
                        "status": 401,
                        "detail": "Session is invalid or expired",
                        "errorCode": "AUTH_002"
                    }
                    """
                    )
                ]
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "userNotFound",
                        summary = "User not found",
                        description = "Example of error when user no longer exists",
                        value = """
                    {
                        "type": "https://api.yourservice.com/errors/USER_001",
                        "title": "User Not Found",
                        "status": 404,
                        "detail": "User could not be found",
                        "errorCode": "USER_001"
                    }
                    """
                    )
                ]
            )]
        )
    ])
    fun validateSession(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<UserResponse> {
        if (userDetails == null) {
            logger.error("No userDetails found in the authentication principal.")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val user = userService.findByUsername(userDetails.username)
        logger.info("Session validated for user: ${user.username}")
        return ResponseEntity.ok(UserResponse.toResponse(user))
    }

}