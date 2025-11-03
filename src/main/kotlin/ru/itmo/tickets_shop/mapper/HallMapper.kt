package ru.itmo.tickets_shop.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mappings
import ru.itmo.tickets_shop.dto.HallDto
import ru.itmo.tickets_shop.entity.Hall

@Mapper(componentModel = "spring")
interface HallMapper {
    @Mappings
    fun mapToHallDto(hall : Hall) : HallDto
}