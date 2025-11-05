package ru.itmo.tickets_shop.entity

import jakarta.persistence.*

@Entity
@Table(name = "ticket")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    val show: Show,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    val seat: Seat?,

    @Enumerated(EnumType.STRING)
    val status: TicketStatus,

    @ManyToMany(mappedBy = "tickets")
    val orders: MutableList<Order> = mutableListOf()
)
