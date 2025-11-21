package ru.itmo.tickets_shop.mapper

import ru.itmo.tickets_shop.dto.SeatDto
import ru.itmo.tickets_shop.dto.SeatStatus
import ru.itmo.tickets_shop.dto.SeatStatusDto
import ru.itmo.tickets_shop.entity.Seat
import ru.itmo.tickets_shop.entity.SeatPrice
import ru.itmo.tickets_shop.entity.Ticket
import ru.itmo.tickets_shop.entity.TicketStatus


fun SeatPrice.toSeatStatusDto(ticket: Ticket?): SeatStatusDto = SeatStatusDto(
    status = mapTicketToSeatStatus(ticket),
    id = seat.id,
    number = seat.seatNumber,
    price = price
)

fun Seat.toSeatDto(): SeatDto = SeatDto(id,rowNumber, seatNumber)

private fun mapTicketToSeatStatus(ticket: Ticket?): SeatStatus {
    if (ticket == null) return SeatStatus.FREE
    if (ticket.status == TicketStatus.RESERVED || ticket.status == TicketStatus.PAID) return SeatStatus.OCCUPIED
    return SeatStatus.OCCUPIED
}
