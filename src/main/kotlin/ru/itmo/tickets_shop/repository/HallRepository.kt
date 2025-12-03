package ru.itmo.tickets_shop.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.tickets_shop.entity.Hall

@Repository
interface HallRepository : JpaRepository<Hall, Long> {
    @Query("from Hall h join fetch h.theatre where h.id = :id")
    fun findHallById(id: Long): Hall?
}
