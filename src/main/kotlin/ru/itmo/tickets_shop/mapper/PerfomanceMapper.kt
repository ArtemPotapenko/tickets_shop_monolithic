package ru.itmo.tickets_shop.mapper

import ru.itmo.tickets_shop.dto.PerformanceDto
import ru.itmo.tickets_shop.entity.Performance

fun Performance.toDto() : PerformanceDto = PerformanceDto(
    id = id,
    title = title,
    description = description,
    durationMinutes = durationMinutes
)