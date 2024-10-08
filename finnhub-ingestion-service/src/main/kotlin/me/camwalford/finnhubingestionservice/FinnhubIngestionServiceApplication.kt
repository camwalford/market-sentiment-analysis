package me.camwalford.finnhubingestionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class FinnhubIngestionServiceApplication

fun main(args: Array<String>) {
	runApplication<FinnhubIngestionServiceApplication>(*args)
}
