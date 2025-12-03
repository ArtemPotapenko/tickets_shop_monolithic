package ru.itmo.tickets_shop.dto

data class HallViewDto(
    val id: Long?,
    val number: Int
)

data class HallDto(
    val id: Long?,
    val number: Int,
    val seatRows: List<SeatRawDto>
)
