package ru.itmo.tickets_shop.mapper

import ru.itmo.tickets_shop.entity.SeatPrice
import ru.itmo.tickets_shop.entity.Ticket
import ru.itmo.tickets_shop.entity.TicketStatus

fun SeatPrice.toTicket(): Ticket = Ticket(
    show = show,
    seat = seat,
    status = TicketStatus.RESERVED
)
