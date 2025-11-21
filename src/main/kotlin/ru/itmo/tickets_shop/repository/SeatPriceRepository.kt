package ru.itmo.tickets_shop.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.tickets_shop.entity.SeatPrice
import ru.itmo.tickets_shop.entity.SeatPriceId

@Repository
interface SeatPriceRepository : JpaRepository<SeatPrice, SeatPriceId> {
    @Query("from SeatPrice seatPrice join fetch seatPrice.seat s where seatPrice.show.id = :showId")
    fun findSeatsByShow(showId: Long) : List<SeatPrice>;

    @Query("from SeatPrice sp join fetch sp.seat s where s.id in :ids and sp.show.id = :showId")
    fun findSeatsByShowIdAndIdIn(showId: Long, ids : List<Long>) : List<SeatPrice>

}
