package ru.itmo.tickets_shop.dto

import java.time.LocalDateTime

data class ShowDto(
    val id: Long,
    val title: String,
    val description: String,
    val date: LocalDateTime,
    val durationMinutes: Int?,
    val hall: HallViewDto,
    val theatre: TheatreViewDto
)

data class ShowViewDto(
    val id: Long,
    val tittle: String,
    val date: LocalDateTime,
)
