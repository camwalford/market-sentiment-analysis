package me.camwalford.backendapiservice.controller

import me.camwalford.backendapiservice.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthContoller {
    @Autowired
    private lateinit var userRepository: UserRepository
}