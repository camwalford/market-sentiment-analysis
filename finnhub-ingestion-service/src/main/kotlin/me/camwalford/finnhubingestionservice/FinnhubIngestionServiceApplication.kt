package me.camwalford.finnhubingestionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FinnhubIngestionServiceApplication

fun main(args: Array<String>) {
	runApplication<FinnhubIngestionServiceApplication>(*args)
}
