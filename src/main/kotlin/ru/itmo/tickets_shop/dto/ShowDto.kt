package ru.itmo.tickets_shop.dto

import java.time.LocalDateTime

data class ShowDto(
    val id: Long,
    val name: String,
    val date: LocalDateTime,
    val description: String,
)
