package ru.itmo.tickets_shop.mapper

import Show
import org.mapstruct.Mapper
import ru.itmo.tickets_shop.dto.TheatreDto
import ru.itmo.tickets_shop.dto.TheatreViewDto
import ru.itmo.tickets_shop.entity.Theatre

@Mapper(componentModel = "spring",
    uses = [TheatreMapper::class, ShowMapper::class])
interface TheatreMapper {
    fun mapToTheatreViewDto(theatre : Theatre) : TheatreViewDto
    fun mapToTheatreDto(theatre : Theatre, shows : List<Show>) : TheatreDto
}
