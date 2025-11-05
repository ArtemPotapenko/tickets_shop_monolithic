package ru.itmo.tickets_shop.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.tickets_shop.entity.Show
import java.time.LocalDateTime

@Repository
interface ShowRepository : JpaRepository<Show, Long> {

    @Query(
        """
        select s from Show s
        join fetch s.performance
        where s.hall.theatre.id = :theatreId
          and s.showTime between :now and :endDate
    """
    )
    fun findAllByTheatreId(
        theatreId: Long,
        now: LocalDateTime,
        endDate: LocalDateTime
    ): List<Show>

    @Query(
        """
        from Show s 
        join fetch s.hall h 
        join fetch s.performance 
        join fetch h.theatre
        where s.id = :id
        """
    )
    fun findShowsByIdFetch(id: Long): Show?;

    @Query(
        """
        from Show s 
        join  s.hall h 
        join fetch s.performance 
        join  h.theatre t
        where t.city = :city
          and s.showTime between :now and :endDate
    """
    )
    fun findShowsByCityAndTimeBetween(city: String, now: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): Page<Show>

}
