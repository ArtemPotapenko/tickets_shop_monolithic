package ru.itmo.tickets_shop.dto

data class TicketDto(
    val id: Long,
    val price: Int,
    val raw: Int,
    val number: Int
)
