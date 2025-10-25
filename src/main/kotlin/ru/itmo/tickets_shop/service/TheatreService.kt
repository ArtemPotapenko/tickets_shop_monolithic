package ru.itmo.tickets_shop.service

import org.springframework.stereotype.Service
import ru.itmo.tickets_shop.dto.TheatreDto
import ru.itmo.tickets_shop.entity.Theatre
import ru.itmo.tickets_shop.repository.ShowRepository
import ru.itmo.tickets_shop.repository.TheatreRepository

@Service
class TheatreService(private val showRepository: ShowRepository,
                     private val theatreRepository: TheatreRepository) {

    fun getAllTheatreInCity(city: String): List<TheatreDto> {
        theatreRepository.findAllByCity(city);
    }
}
