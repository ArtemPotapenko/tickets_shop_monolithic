package ru.itmo.tickets_shop.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import lombok.EqualsAndHashCode
import lombok.ToString


@Entity
@Table(name = "seat")
@ToString
@EqualsAndHashCode
class Seat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val rowNumber: Int,
    val seatNumber: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    val hall: Hall
)