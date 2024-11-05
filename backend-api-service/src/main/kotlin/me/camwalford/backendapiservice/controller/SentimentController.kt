package me.camwalford.backendapiservice.controller

import me.camwalford.backendapiservice.service.SentimentService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/sentiment")
class SentimentController(
    private val sentimentService: SentimentService
) {

}