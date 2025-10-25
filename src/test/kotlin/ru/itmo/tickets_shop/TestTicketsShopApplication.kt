package ru.itmo.tickets_shop

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<TicketsShopApplication>().with(TestcontainersConfiguration::class).run(*args)
}
