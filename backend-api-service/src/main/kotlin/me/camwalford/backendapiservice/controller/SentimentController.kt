//package me.camwalford.backendapiservice.controller
//
//import me.camwalford.backendapiservice.dto.SentimentRequest
//import me.camwalford.backendapiservice.dto.SentimentResponse
//import me.camwalford.backendapiservice.service.SentimentService
//import me.camwalford.backendapiservice.service.UserService
//import org.slf4j.LoggerFactory
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.security.core.annotation.AuthenticationPrincipal
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.web.bind.annotation.CrossOrigin
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//import org.springframework.web.server.ResponseStatusException
//
//@CrossOrigin(origins = ["http://localhost:3000"])
//@RestController
//@RequestMapping("/api/sentiment")
//class SentimentController(
//    private val sentimentService: SentimentService,
//    private val userService: UserService
//) {
//    private val logger = LoggerFactory.getLogger(SentimentController::class.java)
//
//    @PostMapping
//    fun getSentiment(
//        @AuthenticationPrincipal userDetails: UserDetails,
//        @RequestBody request: SentimentRequest
//    ): ResponseEntity<SentimentResponse> {
//        logger.info("Processing sentiment request for tickers: ${request.tickers} by user: ${userDetails.username}")
//
//        // Fetch the full User entity from the database
//        val user = userService.findByEmail(userDetails.username)
//            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.")
//
//        val response = sentimentService.processSentimentRequest(user, request)
//
//        return ResponseEntity.ok(response)
//    }
//}