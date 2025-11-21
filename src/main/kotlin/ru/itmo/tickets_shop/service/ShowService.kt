package ru.itmo.tickets_shop.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import ru.itmo.tickets_shop.dto.SeatRawDto
import ru.itmo.tickets_shop.dto.ShowDto
import ru.itmo.tickets_shop.dto.ShowViewDto
import ru.itmo.tickets_shop.entity.TicketStatus
import ru.itmo.tickets_shop.exception.ShowNotFoundException
import ru.itmo.tickets_shop.mapper.toDto
import ru.itmo.tickets_shop.mapper.toSeatStatusDto
import ru.itmo.tickets_shop.mapper.toViewDto
import ru.itmo.tickets_shop.repository.SeatPriceRepository
import ru.itmo.tickets_shop.repository.ShowRepository
import ru.itmo.tickets_shop.repository.TicketRepository
import java.time.LocalDateTime

@Service
open class ShowService(
    private val showRepository: ShowRepository,
    private val seatPriceRepository: SeatPriceRepository,
    private val ticketRepository: TicketRepository,
) {
    private val log = LoggerFactory.getLogger(ShowService::class.java)

    open suspend fun getShowInfo(id: Long): ShowDto {
        log.info("Получение информации о шоу id={}", id)
        val show = showRepository.findShowsByIdFetch(id)
            ?: throw ShowNotFoundException("Шоу по id $id не найдено")
        log.info("Найдено шоу: {}", show.performance.title)
        return show.toDto()
    }

    open suspend fun getAllShow(city: String, page: Int, pageSize: Int): Page<ShowViewDto> {
        log.info("Получение всех шоу в городе={}, страница={}, размер страницы={}", city, page, pageSize)
        val shows = showRepository.findShowsByCityAndTimeBetween(
            city,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(14),
            PageRequest.of(page - 1, pageSize)
        ).map { it.toViewDto() }
        log.info("Найдено шоу: {}", shows.map { it.tittle })
        return shows
    }

    open suspend fun getAllSeats(showId: Long): List<SeatRawDto> {
        log.info("Получение всех мест для шоу id={}", showId)
        val seats = seatPriceRepository.findSeatsByShow(showId)
        val ids = seats.map { it.seat.id }
        val tickets = ticketRepository.findAllBySeatIdInAndShowId(ids, showId, TicketStatus.CANCELLED)
        val mapSeatToTicket = tickets.associateBy { it.seat!!.id }

        val mutableList = mutableListOf<SeatRawDto>()
        val groupedSeats = seats.groupBy { it.seat.rowNumber }

        groupedSeats.forEach { (row, seatPrices) ->
            mutableList.add(
                SeatRawDto(
                    row,
                    seatPrices.map { it.toSeatStatusDto(mapSeatToTicket[it.seat.id]) }
                )
            )
        }

        log.info("Всего рядов мест: {}", mutableList.size)
        return mutableList
    }
}
