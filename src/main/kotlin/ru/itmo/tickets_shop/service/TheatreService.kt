package ru.itmo.tickets_shop.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.tickets_shop.dto.HallDto
import ru.itmo.tickets_shop.dto.TheatreDto
import ru.itmo.tickets_shop.dto.TheatrePayload
import ru.itmo.tickets_shop.dto.TheatreViewDto
import ru.itmo.tickets_shop.entity.Hall
import ru.itmo.tickets_shop.entity.Seat
import ru.itmo.tickets_shop.exception.TheatreNotFoundException
import ru.itmo.tickets_shop.mapper.toDto
import ru.itmo.tickets_shop.mapper.toEntity
import ru.itmo.tickets_shop.mapper.toPayload
import ru.itmo.tickets_shop.mapper.toViewDto
import ru.itmo.tickets_shop.repository.HallRepository
import ru.itmo.tickets_shop.repository.SeatRepository
import ru.itmo.tickets_shop.repository.ShowRepository
import ru.itmo.tickets_shop.repository.TheatreRepository
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
open class TheatreService(
    private val showRepository: ShowRepository,
    private val theatreRepository: TheatreRepository,
    private val hallRepository: HallRepository,
    private val seatRepository: SeatRepository,
) {

    private val log = LoggerFactory.getLogger(TheatreService::class.java)

    open fun getAllTheatreInCity(city: String, page: Int, pageSize: Int): Page<TheatreViewDto> {
        log.info("Получение всех театров в городе={}, страница={}, размер страницы={}", city, page, pageSize)
        val theatres = theatreRepository.findAllByCity(city, PageRequest.of(page - 1, pageSize))
            .map { it.toViewDto() }
        log.info("Найдено театров: {}", theatres.totalElements)
        return theatres
    }

    open fun getTheatreInfo(id: Long): TheatreDto {
        log.info("Получение информации о театре id={}", id)
        val theatre = theatreRepository.findTheatreByIdFetchHall(id)
            ?: throw TheatreNotFoundException("Theatre not found with id $id")

        val shows = showRepository.findAllByTheatreId(
            theatre.id,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(14)
        )
        log.info("Найдено {} шоу для театра id={}", shows.size, id)
        return theatre.toDto(shows)
    }

    @Transactional
    open fun createTheatre(theatre: TheatrePayload): TheatrePayload {
        log.info("Создание театра с названием={}", theatre.name)
        if (theatre.id != null) throw IllegalArgumentException("ID должен быть null при создании")
        val saved = theatreRepository.save(theatre.toEntity()).toPayload()
        log.info("Театр создан с id={}", saved.id)
        return saved
    }

    @Transactional
    open fun updateTheatre(theatre: TheatrePayload): TheatrePayload {
        log.info("Обновление театра id={}", theatre.id)
        if (theatre.id == null) throw IllegalArgumentException("ID должен быть не null при обновлении")
        if (!theatreRepository.existsById(theatre.id)) throw TheatreNotFoundException("Theatre not found with id ${theatre.id}")
        val updated = theatreRepository.save(theatre.toEntity()).toPayload()
        log.info("Театр обновлен id={}", updated.id)
        return updated
    }

    @Transactional
    open fun createHall(theatreId: Long, dto: HallDto): HallDto {
        val theatre = theatreRepository.findTheatresById(theatreId)
            ?: throw RuntimeException("Theatre not found")

        val hall = Hall(number = dto.number, theatre = theatre)
        val savedHall = hallRepository.save(hall)

        dto.seatRows.forEach { row ->
            row.seats.forEach { seat ->
                seatRepository.save(Seat(rowNumber = row.row, seatNumber = seat.number, hall = savedHall))
            }
        }

        return savedHall.toDto(dto.seatRows)
    }

    @Transactional
    open fun updateHall(id: Long, dto: HallDto): HallDto {
        val hall = hallRepository.findHallById(id)
            ?: throw RuntimeException("Hall not found")

        hall.number = dto.number
        hall.seats.clear()
        dto.seatRows.forEach { row ->
            row.seats.forEach { seat ->
                seatRepository.save(Seat(rowNumber = row.row, seatNumber = seat.number, hall = hall))
            }
        }
        hallRepository.save(hall)
        return hall.toDto(dto.seatRows)
    }

    @Transactional
    open fun deleteHall(id: Long) {
        if (!hallRepository.existsById(id)) throw RuntimeException("Hall not found")
        hallRepository.deleteById(id)
    }
}