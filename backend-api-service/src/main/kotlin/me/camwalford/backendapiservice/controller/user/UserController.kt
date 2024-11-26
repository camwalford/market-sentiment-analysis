package me.camwalford.backendapiservice.controller.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import me.camwalford.backendapiservice.service.RequestService
import me.camwalford.backendapiservice.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail

@RestController
@RequestMapping("/api/user")
@Tag(name = "Users", description = "User management operations")
class UserController(
    private val userService: UserService,
    private val requestService: RequestService
) {
    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Get current user",
        description = "Returns the profile of the currently authenticated user",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "User stats retrieved successfully",
            content = [Content(schema = Schema(implementation = UserStatsResponse::class))]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            ref = "#/components/responses/UnauthorizedError"
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
                        description = "Example of error when user doesn't exist",
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
    fun getCurrentUser(@AuthenticationPrincipal userDetails: UserDetails): UserStatsResponse {
        logger.info("Fetching current user stat profile: ${userDetails.username}")
        return requestService.getUserStatsByUsername(userDetails.username)
    }


    // Admin endpoints below
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "List all users",
        description = "Returns a list of all users. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Users list retrieved successfully",
            content = [Content(schema = Schema(implementation = List::class))]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            ref = "#/components/responses/UnauthorizedError"
        ),
        ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions",
            content = [Content(
                mediaType = "application/problem+json",
                schema = Schema(implementation = ProblemDetail::class),
                examples = [
                    ExampleObject(
                        name = "insufficientPermissions",
                        summary = "Insufficient permissions",
                        description = "Example of error when non-admin tries to access",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/AUTH_001",
                            "title": "Access Denied",
                            "status": 403,
                            "detail": "Insufficient permissions to access this resource",
                            "errorCode": "AUTH_001"
                        }
                        """
                    )
                ]
            )]
        )
    ])
    fun listAll(): List<UserResponse> {
        logger.info("Admin: Listing all users")
        return userService.findAll().map { UserResponse.toResponse(it) }
    }


    @GetMapping("/user-requests")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "List all requests",
        description = "Returns a list of all requests. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Request statistics retrieved successfully",
            content = [Content(schema = Schema(implementation = List::class))]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            ref = "#/components/responses/UnauthorizedError"
        ),
        ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions",
            ref = "#/components/responses/ForbiddenError"
        )
    ])
    fun listAllRequests(): List<UserStatsResponse> {
        logger.info("Admin: Listing all requests")
        return requestService.getAllUserStats()
    }

    @GetMapping("/endpoint-requests")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "List all requests by endpoint",
        description = "Returns a list of all requests grouped by endpoint. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Endpoint statistics retrieved successfully",
            content = [Content(schema = Schema(implementation = List::class))]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            ref = "#/components/responses/UnauthorizedError"
        ),
        ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions",
            ref = "#/components/responses/ForbiddenError"
        )
    ])
    fun listAllEndpointRequests(): List<EndpointStatsResponse> {
        logger.info("Admin: Listing all requests by endpoint")
        return requestService.getEndpointStats()
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get user by ID",
        description = "Returns a specific user by ID. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "User retrieved successfully",
            content = [Content(schema = Schema(implementation = UserResponse::class))]
        ),
        ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            ref = "#/components/responses/UnauthorizedError"
        ),
        ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions",
            ref = "#/components/responses/ForbiddenError"
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
                        description = "Example of error when user ID doesn't exist",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/USER_001",
                            "title": "User Not Found",
                            "status": 404,
                            "detail": "User with ID {id} could not be found",
                            "errorCode": "USER_001"
                        }
                        """
                    )
                ]
            )]
        )
    ])
    fun getUserById(@PathVariable id: Long): UserResponse {
        logger.info("Admin: Fetching user with ID: $id")
        return userService.findById(id)
            ?.let { UserResponse.toResponse(it) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete user",
        description = "Deletes a user account. Requires ADMIN role.",
        security = [SecurityRequirement(name = "bearer-auth")]
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "User successfully deleted"
        ),
        ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            ref = "#/components/responses/UnauthorizedError"
        ),
        ApiResponse(
            responseCode = "403",
            description = "Insufficient permissions",
            ref = "#/components/responses/ForbiddenError"
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
                        description = "Example of error when user ID doesn't exist",
                        value = """
                        {
                            "type": "https://api.yourservice.com/errors/USER_001",
                            "title": "User Not Found",
                            "status": 404,
                            "detail": "User with ID {id} could not be found",
                            "errorCode": "USER_001"
                        }
                        """
                    )
                ]
            )]
        )
    ])
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("Admin: Deleting user with ID: $id")
        return if (userService.deleteById(id)) {
            ResponseEntity.noContent().build()
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }
    }
}