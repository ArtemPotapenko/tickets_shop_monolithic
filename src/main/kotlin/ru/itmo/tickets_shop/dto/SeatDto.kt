package ru.itmo.tickets_shop.dto

data class SeatRawDto(val row: Int, val seats: List<SeatStatusDto>)

data class SeatStatusDto
    (
    val id: Long,
    val status: SeatStatus,
    val number: Int,
    val price: Int
)
data class SeatDto
    (
    val id: Long,
    val raw: Int,
    val number: Int,
)