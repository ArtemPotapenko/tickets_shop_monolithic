package ru.itmo.tickets_shop.dto

import java.time.LocalDateTime

data class ShowViewDto(
    val id: Long,
    val name: String,
    val date: LocalDateTime,
)
