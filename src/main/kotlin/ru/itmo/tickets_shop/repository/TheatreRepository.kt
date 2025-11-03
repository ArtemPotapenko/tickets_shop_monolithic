package ru.itmo.tickets_shop.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.itmo.tickets_shop.entity.Theatre

@Repository
interface TheatreRepository : JpaRepository<Theatre, Long> {
    @Query("from Theatre t where t.city = :city")
    fun findAllByCity(city: String): List<Theatre>;
    @Query("from Theatre t join fetch t.halls where t.id = :id")
    fun findTheatreByIdFetchHall(id: Long): Theatre?;
}