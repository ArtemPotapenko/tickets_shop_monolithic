package ru.itmo.tickets_shop.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.tickets_shop.entity.Ticket
import ru.itmo.tickets_shop.entity.TicketStatus

@Repository
interface TicketRepository : JpaRepository<Ticket, Long> {
    @Query("from Ticket t join fetch t.seat s where t.show.id = :showId and s.id = :seatId and not(t.status = :canceledStatus)")
    fun findAllBySeatIdInAndShowId(seatId: Collection<Long>, showId: Long, canceledStatus : TicketStatus) : List<Ticket>
}
