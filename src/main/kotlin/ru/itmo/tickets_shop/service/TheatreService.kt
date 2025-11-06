package ru.itmo.tickets_shop.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.tickets_shop.dto.TheatreDto
import ru.itmo.tickets_shop.dto.TheatrePayload
import ru.itmo.tickets_shop.dto.TheatreViewDto
import ru.itmo.tickets_shop.exception.TheatreNotFoundException
import ru.itmo.tickets_shop.mapper.toDto
import ru.itmo.tickets_shop.mapper.toPayload
import ru.itmo.tickets_shop.mapper.toTheatre
import ru.itmo.tickets_shop.mapper.toViewDto
import ru.itmo.tickets_shop.repository.ShowRepository
import ru.itmo.tickets_shop.repository.TheatreRepository
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
open class TheatreService(
    private val showRepository: ShowRepository,
    private val theatreRepository: TheatreRepository
) {

    open suspend fun getAllTheatreInCity(city: String, page: Int, pageSize: Int): Page<TheatreViewDto> {
        return theatreRepository.findAllByCity(city, PageRequest.of(page - 1, pageSize))
            .map { it.toViewDto() }
    }

    open suspend fun getTheatreInfo(id: Long): TheatreDto {
        val theatre = theatreRepository.findTheatreByIdFetchHall(id)
            ?: throw TheatreNotFoundException("Theatre not found with id $id")

        val shows = showRepository.findAllByTheatreId(
            theatre.id,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(14)
        )
        return theatre.toDto(shows)
    }

    @Transactional
    open suspend fun createTheatre(theatre: TheatrePayload): TheatrePayload {
        if (theatre.id != null) {
            throw IllegalArgumentException("ID должен быть null при создании")
        }
        return theatreRepository.save(
            theatre.toTheatre()
        ).toPayload()
    }

    @Transactional
    open suspend fun updateTheatre(theatre: TheatrePayload): TheatrePayload {
        if (theatre.id == null) {
            throw IllegalArgumentException("ID должен быть не null при обновлении")
        }
        if (!theatreRepository.existsById(theatre.id)) {
            throw TheatreNotFoundException("Theatre not found with id ${theatre.id}")
        }
        return theatreRepository.save(
            theatre.toTheatre()
        ).toPayload()
    }
}
