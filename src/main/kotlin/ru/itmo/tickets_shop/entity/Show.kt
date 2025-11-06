package ru.itmo.tickets_shop.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "show")
class Show(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    var performance: Performance,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    var hall: Hall,

    val showTime: LocalDateTime,

    @OneToMany(mappedBy = "show")
    val seatPrices: MutableList<SeatPrice> = mutableListOf(),

    @OneToMany(mappedBy = "show")
    val tickets: MutableList<Ticket> = mutableListOf()
)
{
    constructor() : this(0, Performance(), Hall(), LocalDateTime.now())
}