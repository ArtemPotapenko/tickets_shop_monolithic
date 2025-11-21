package ru.itmo.tickets_shop.dto

import ru.itmo.tickets_shop.entity.OrderStatus
import java.time.LocalDateTime

data class OrderDto (
    val id: Long,
    val createdAt: LocalDateTime,
    val reservedAt: LocalDateTime?,
    val status: OrderStatus = OrderStatus.RESERVED,
    val seat : List<SeatDto>,
    val price: Int
    )

data class OrderPayload(
    val seatIds : List<Long>,
    val showId : Long
)