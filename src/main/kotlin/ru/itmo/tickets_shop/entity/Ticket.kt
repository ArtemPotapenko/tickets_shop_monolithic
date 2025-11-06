package ru.itmo.tickets_shop.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "ticket")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    val show: Show,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    val seat: Seat?,

    @Enumerated(EnumType.STRING)
    var status: TicketStatus,

    @ManyToMany(mappedBy = "tickets")
    var orders: MutableList<Order> = mutableListOf()
)
{
    constructor() : this(0, Show(), Seat(), TicketStatus.RESERVED)
}