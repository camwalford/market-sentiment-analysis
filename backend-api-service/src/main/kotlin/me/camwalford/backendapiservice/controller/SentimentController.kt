package me.camwalford.backendapiservice.controller

import me.camwalford.backendapiservice.service.SentimentService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/sentiment")
class SentimentController(
    private val sentimentService: SentimentService
) {

}