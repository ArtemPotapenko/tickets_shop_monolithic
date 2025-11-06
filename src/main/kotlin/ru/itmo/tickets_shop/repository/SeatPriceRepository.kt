package ru.itmo.tickets_shop.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.tickets_shop.entity.SeatPrice
import ru.itmo.tickets_shop.entity.SeatPriceId
import ru.itmo.tickets_shop.entity.Show

@Repository
interface SeatPriceRepository : JpaRepository<SeatPrice, SeatPriceId> {
    @Query("from SeatPrice seatPrice join fetch seatPrice.seat s where seatPrice.show.id = :showId")
    fun findSeatsByShow(showId: Long) : List<SeatPrice>;
}
