package ru.itmo.tickets_shop.dto

data class PerformanceDto(
    val id: Long?,
    val title: String,
    val description: String?,
    val durationMinutes: Int?
)