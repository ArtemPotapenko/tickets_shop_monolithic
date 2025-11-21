package ru.itmo.tickets_shop.entity

data class TicketProjection(
    val id: Long,
    val price: Int,
    val rowNumber: Int,
    val seatNumber: Int
)
