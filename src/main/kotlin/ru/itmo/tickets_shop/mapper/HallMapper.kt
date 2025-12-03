package ru.itmo.tickets_shop.mapper

import ru.itmo.tickets_shop.dto.HallDto
import ru.itmo.tickets_shop.dto.HallViewDto
import ru.itmo.tickets_shop.dto.SeatRawDto
import ru.itmo.tickets_shop.entity.Hall
import ru.itmo.tickets_shop.entity.Theatre


fun Hall.toViewDto() : HallViewDto =
    HallViewDto(
        id = id,
        number = number
    )

fun HallViewDto.toEntity(theatre : Theatre) : Hall =
    Hall(
        id = id ?: 0,
        number = number,
        seats = mutableListOf(),
        theatre = theatre
    )

fun Hall.toDto(seatRaws : List<SeatRawDto>) : HallDto = HallDto(
    id = id,
    number = number,
    seatRows = seatRaws,
)