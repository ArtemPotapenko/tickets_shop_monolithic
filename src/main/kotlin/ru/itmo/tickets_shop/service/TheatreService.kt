package ru.itmo.tickets_shop.service

import org.springframework.stereotype.Service
import ru.itmo.tickets_shop.dto.TheatreDto
import ru.itmo.tickets_shop.dto.TheatreViewDto
import ru.itmo.tickets_shop.entity.Theatre
import ru.itmo.tickets_shop.exception.TheatreNotFoundException
import ru.itmo.tickets_shop.mapper.TheatreMapper
import ru.itmo.tickets_shop.repository.ShowRepository
import ru.itmo.tickets_shop.repository.TheatreRepository
import java.time.LocalDateTime

@Service
class TheatreService(
    private val showRepository: ShowRepository,
    private val theatreRepository: TheatreRepository, private val theatreMapper: TheatreMapper
) {

    fun getAllTheatreInCity(city: String): List<TheatreViewDto> {
        return theatreRepository.findAllByCity(city)
            .stream().map { theatreMapper.mapToTheatreViewDto(it) }.toList()
    }

    fun getTheatreInfo(id: Long): TheatreDto {
        var theatre = theatreRepository.findTheatreByIdFetchHall(id)
            ?: throw TheatreNotFoundException("Theatre not found with id $id")

        var shows = showRepository
            .findAllByTheatreId(
                theatre.id,
                LocalDateTime.now(), LocalDateTime.now().plusDays(14)
            )
        return theatreMapper.mapToTheatreDto(theatre, shows)
    }

}
