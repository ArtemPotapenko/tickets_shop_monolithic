package ru.itmo.tickets_shop.repository

import Show
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ShowRepository : JpaRepository<Show, String> {
    @Query("""
    from Show s join fetch s.performance
    where s.hall.theatre.id = :theatreId 
      and s.showTime between :now and :endDate
""")
    fun findAllByTheatreId(
        theatreId: Long,
        now: LocalDateTime,
        endDate: LocalDateTime
    ): List<Show>

}