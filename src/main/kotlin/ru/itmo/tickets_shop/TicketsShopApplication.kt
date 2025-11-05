package ru.itmo.tickets_shop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class TicketsShopApplication

fun main(args: Array<String>) {
	runApplication<TicketsShopApplication>(*args)
}
