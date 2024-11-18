package me.camwalford.finnhubingestionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class FinnhubIngestionServiceApplication

fun main(args: Array<String>) {
	runApplication<FinnhubIngestionServiceApplication>(*args)
}