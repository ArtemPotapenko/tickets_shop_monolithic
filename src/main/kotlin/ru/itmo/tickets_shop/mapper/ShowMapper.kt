package ru.itmo.tickets_shop.mapper

import ru.itmo.tickets_shop.dto.ShowDto
import ru.itmo.tickets_shop.dto.ShowViewDto
import ru.itmo.tickets_shop.entity.Show
import ru.itmo.tickets_shop.exception.TheatreNotFoundException

fun Show.toViewDto(): ShowViewDto = ShowViewDto(
    id = id,
    tittle = performance.title,
    date = showTime
)

fun Show.toDto(): ShowDto = ShowDto(
    id = id,
    title = performance.title,
    description = performance.description?:"",
    date = showTime,
    hall = hall.toViewDto(),
    durationMinutes = performance.durationMinutes,
    theatre = (hall.theatre?: throw TheatreNotFoundException("У данного шоу не найден театр")).toViewDto()
)
