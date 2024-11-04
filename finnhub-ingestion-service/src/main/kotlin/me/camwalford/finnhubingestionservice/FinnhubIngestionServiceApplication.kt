package me.camwalford.finnhubingestionservice

import me.camwalford.finnhubingestionservice.service.CompanyNewsFetcher
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
class FinnhubIngestionServiceApplication(
	private val companyNewsFetcher: CompanyNewsFetcher
) {
	@Bean
	fun runOnceAtStartup() = CommandLineRunner {
		companyNewsFetcher.fetchAndSendCompanyNews(company = "AAPL")
	}

	fun main(args: Array<String>) {
		runApplication<FinnhubIngestionServiceApplication>(*args)
	}
}