package ru.itmo.tickets_shop.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table


@Entity
@Table(name = "seat")
class Seat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    val rowNumber: Int,
    val seatNumber: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    val hall: Hall
)
{
    constructor() : this(0,0,0, Hall())
}