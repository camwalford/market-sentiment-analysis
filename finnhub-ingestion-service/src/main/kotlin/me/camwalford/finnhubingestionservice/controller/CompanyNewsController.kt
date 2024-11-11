package me.camwalford.finnhubingestionservice.controller


import me.camwalford.finnhubingestionservice.service.CompanyNewsFetcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class CompanyNewsController(
    private val companyNewsFetcher: CompanyNewsFetcher
) {
     @PostMapping("/company-news/")
     fun fetchCompanyNews(@RequestBody ticker: String, @RequestBody from: String, @RequestBody to: String) {
         companyNewsFetcher.fetchAndSendCompanyNews(ticker, from, to)
     }

}