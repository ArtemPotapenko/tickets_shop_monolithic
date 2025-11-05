package ru.itmo.tickets_shop.mapper

import ru.itmo.tickets_shop.dto.HallViewDto
import ru.itmo.tickets_shop.entity.Hall
import ru.itmo.tickets_shop.entity.Theatre


fun Hall.toDto() : HallViewDto =
    HallViewDto(
        id = id,
        number = number
    )

fun HallViewDto.toHall(theatre : Theatre) : Hall =
    Hall(
        id = id ?: 0,
        number = number,
        seats = mutableListOf(),
        theatre = theatre
    )