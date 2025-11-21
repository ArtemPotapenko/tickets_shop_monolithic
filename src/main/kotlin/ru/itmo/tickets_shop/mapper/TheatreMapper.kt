package ru.itmo.tickets_shop.mapper

import ru.itmo.tickets_shop.dto.TheatreDto
import ru.itmo.tickets_shop.dto.TheatrePayload
import ru.itmo.tickets_shop.dto.TheatreViewDto
import ru.itmo.tickets_shop.entity.Show
import ru.itmo.tickets_shop.entity.Theatre
import kotlin.collections.map

fun Theatre.toDto(shows: List<Show>): TheatreDto =
    TheatreDto(
        id = id,
        name = name,
        city = city,
        address = address,
        halls = halls.map { it.toDto() },
        shows = shows.map { it.toViewDto() }
    )

fun Theatre.toViewDto(): TheatreViewDto = TheatreViewDto(
    id = id,
    name = name,
    city = city,
    address = address
)

fun TheatrePayload.toTheatre(): Theatre {
    val theatre = Theatre(
        id = id ?: 0,
        name = name,
        city = city,
        address = address,
        halls = mutableListOf(),
        performances = mutableListOf(),
    )
    theatre.halls = halls.map { it.toHall(theatre) }.toMutableList()
    return theatre
}

fun Theatre.toPayload(): TheatrePayload = TheatrePayload(
    id = id,
    name = name,
    city = city,
    address = address,
    halls = halls.map { it.toDto() }
)