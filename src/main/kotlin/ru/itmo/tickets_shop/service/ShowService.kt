package ru.itmo.tickets_shop.service

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
    private val SeatPriceRepository: SeatPriceRepository,
    private val ticketRepository: TicketRepository,
) {
    open suspend fun getShowInfo(id: Long): ShowDto =
        (showRepository.findShowsByIdFetch(id)
            ?: throw ShowNotFoundException("Шоу по id $id не найдено")
                ).toDto()

    open suspend fun getAllShow(city: String, page: Int, pageSize: Int): Page<ShowViewDto> =
        showRepository.findShowsByCityAndTimeBetween(
            city, LocalDateTime.now(),
            LocalDateTime.now().plusDays(14), PageRequest.of(page - 1, pageSize)
        ).map { it.toViewDto() }

    open suspend fun getAllSeats(showId: Long): List<SeatRawDto> {
        val seats = SeatPriceRepository.findSeatsByShow(showId);
        val ids = seats.map { it.seat.id }
        val tickets = ticketRepository.findAllBySeatIdInAndShowId(ids, showId, TicketStatus.CANCELLED)
        val mapSeatToTicket = tickets.associateBy { it.seat!!.id }
        val mutableList = mutableListOf<SeatRawDto>()
        val groupedSeats = seats.groupBy { it.seat.rowNumber }
        groupedSeats.forEach {
            mutableList.add(
                SeatRawDto(
                    it.key, it.value.map
                    {
                        it.toSeatStatusDto(mapSeatToTicket.get(it.seat.id))
                    })
            )
        }
        return mutableList
    }

}
