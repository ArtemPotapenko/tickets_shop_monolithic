package ru.itmo.tickets_shop.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.tickets_shop.entity.Seat
import ru.itmo.tickets_shop.entity.SeatPrice
import ru.itmo.tickets_shop.entity.Show

@Repository
interface SeatRepository : JpaRepository<Seat, Long> {
    @Query("from SeatPrice seatPrice join fetch seatPrice.seat s where seatPrice.show.id = :seatId")
    fun findSeatsByShow(showId: Long) : List<SeatPrice>;
}
