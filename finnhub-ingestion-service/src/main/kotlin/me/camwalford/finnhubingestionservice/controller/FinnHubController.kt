package me.camwalford.finnhubingestionservice.controller


import jakarta.validation.Valid
import me.camwalford.finnhubingestionservice.model.MarketNewsRequest
import me.camwalford.finnhubingestionservice.service.FinnHubService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/finnhub")
class FinnHubController(private val finnHubService: FinnHubService) {

    @PostMapping("/market-news", produces = ["application/x-protobuf"])
    fun getMarketNews(@Valid @RequestBody request: MarketNewsRequest) =
        finnHubService.fetchMarketNewsList(request.category, request.minId)
}