package me.camwalford.postgresdbservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostgresdbServiceApplication

fun main(args: Array<String>) {
	runApplication<PostgresdbServiceApplication>(*args)
}
