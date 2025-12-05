package ru.itmo.tickets_shop.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import ru.itmo.tickets_shop.dto.PerformanceDto
import ru.itmo.tickets_shop.dto.SeatRawDto
import ru.itmo.tickets_shop.dto.ShowDto
import ru.itmo.tickets_shop.dto.ShowViewDto
import ru.itmo.tickets_shop.entity.Performance
import ru.itmo.tickets_shop.entity.TicketStatus
import ru.itmo.tickets_shop.exception.ShowNotFoundException
import ru.itmo.tickets_shop.mapper.toDto
import ru.itmo.tickets_shop.mapper.toSeatStatusDto
import ru.itmo.tickets_shop.mapper.toViewDto
import ru.itmo.tickets_shop.repository.PerformanceRepository
import ru.itmo.tickets_shop.repository.SeatPriceRepository
import ru.itmo.tickets_shop.repository.ShowRepository
import ru.itmo.tickets_shop.repository.TicketRepository
import java.time.LocalDateTime

fun <T> List<T>.toPage(page: Int, pageSize: Int): Page<T> {
    val total = this.size
    val fromIndex = ((page - 1) * pageSize).coerceAtLeast(0)
    val toIndex = (fromIndex + pageSize).coerceAtMost(total)
    val content = if (fromIndex >= total) emptyList() else this.subList(fromIndex, toIndex)
    return PageImpl(content, PageRequest.of(page - 1, pageSize), total.toLong())
}

@Service
class ShowService(
    private val showRepository: ShowRepository,
    private val seatPriceRepository: SeatPriceRepository,
    private val ticketRepository: TicketRepository,
    private val performanceRepository: PerformanceRepository,
) {
    private val log = LoggerFactory.getLogger(ShowService::class.java)

    fun getShowInfo(id: Long): ShowDto {
        log.info("Получение информации о шоу id={}", id)

        val show = showRepository.findShowsByIdFetch(id)
            ?: throw ShowNotFoundException("Шоу по id $id не найдено")

        log.info("Найдено шоу: {}", show.performance.title)
        return show.toDto()
    }

    fun getAllShow(city: String, page: Int, pageSize: Int): Page<ShowViewDto> {
        log.info("Получение всех шоу в городе={}, страница={}, размер={}", city, page, pageSize)

        val shows = showRepository.findShowsByCityAndTimeBetween(
            city,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(14),
            PageRequest.of(page - 1, pageSize)
        )

        return shows.map { it.toViewDto() }
    }

    fun getAllSeats(showId: Long, page: Int, pageSize: Int): Page<SeatRawDto> {
        log.info("Получение всех мест для шоу id={}, page={}, pageSize={}", showId, page, pageSize)

        val seats = seatPriceRepository.findSeatsByShow(showId)
        val ids = seats.map { it.seat.id }
        val tickets = ticketRepository.findAllBySeatIdInAndShowId(ids, showId, TicketStatus.CANCELLED)
        val mapSeatToTicket = tickets.associateBy { it.seat!!.id }

        val rows = seats
            .groupBy { it.seat.rowNumber }
            .map { (row, seatPrices) ->
                SeatRawDto(
                    row,
                    seatPrices.map { it.toSeatStatusDto(mapSeatToTicket[it.seat.id]) }
                )
            }
            .sortedBy { it.row }

        return rows.toPage(page, pageSize)
    }

    fun createPerformance(dto: PerformanceDto): PerformanceDto {
        val entity = performanceRepository.save(
            Performance(
                title = dto.title,
                description = dto.description,
                durationMinutes = dto.durationMinutes
            )
        )
        return entity.toDto()
    }

    fun updatePerformance(id: Long, dto: PerformanceDto): PerformanceDto {
        val performance = performanceRepository.findById(id)
            .orElseThrow { RuntimeException("Performance not found") }

        performance.title = dto.title
        performance.description = dto.description
        performance.durationMinutes = dto.durationMinutes

        return performanceRepository.save(performance).toDto()
    }
}
