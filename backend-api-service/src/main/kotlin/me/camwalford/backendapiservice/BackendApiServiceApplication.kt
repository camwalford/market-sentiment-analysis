package me.camwalford.backendapiservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackendApiServiceApplication

fun main(args: Array<String>) {
	runApplication<BackendApiServiceApplication>(*args)
}
