package ru.itmo.tickets_shop.entity

import jakarta.persistence.*

@Entity
@Table(name = "seat_price")
class SeatPrice(
    @EmbeddedId
    val id: SeatPriceId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seatId")
    @JoinColumn(name = "seat_id")
    val seat: Seat,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("showId")
    @JoinColumn(name = "show_id")
    val show: Show,

    val price: Int
)

@Embeddable
data class SeatPriceId(
    val seatId: Long = 0,
    val showId: Long = 0
)
