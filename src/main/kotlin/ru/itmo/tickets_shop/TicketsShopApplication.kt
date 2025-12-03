package ru.itmo.tickets_shop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
open class TicketsShopApplication

fun main(args: Array<String>) {
	runApplication<TicketsShopApplication>(*args)
}
