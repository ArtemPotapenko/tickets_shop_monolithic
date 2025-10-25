package ru.itmo.tickets_shop.entity

import Show
import jakarta.persistence.*
import lombok.EqualsAndHashCode
import lombok.ToString

@Entity
@Table(name = "ticket")
@ToString
@EqualsAndHashCode
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    val show: Show,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    val seat: Seat,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val order: Order
)
