package ru.itmo.tickets_shop.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.tickets_shop.entity.Ticket
import ru.itmo.tickets_shop.entity.TicketProjection
import ru.itmo.tickets_shop.entity.TicketStatus


@Repository
interface TicketRepository : JpaRepository<Ticket, Long> {
    @Query(
        """from Ticket t join fetch t.seat s 
                where t.show.id = :showId
                and s.id in :seatIds
        and t.status <> :canceledStatus"""
    )
    fun findAllBySeatIdInAndShowId(
        seatIds: Collection<Long>,
        showId: Long,
        canceledStatus: TicketStatus
    ): List<Ticket>

    @Query("from Ticket t left join fetch t.seat s join t.orders o where o.id = :orderId")
    fun findAllByOrder(orderId : Long): List<Ticket>

    @Query(
        """
        select t.id AS id,
               sp.price AS price,
               s.rowNumber AS rowNumber,
               s.seatNumber AS seatNumber
        from Ticket t
        join t.seat s
        join SeatPrice sp ON sp.seat.id = s.id AND sp.show.id = t.show.id
        join t.orders o
        where o.id = :orderId
    """
    )
    fun findTicketsByOrderId(
        orderId: Long,
        pageable: Pageable
    ): Page<TicketProjection>;
}