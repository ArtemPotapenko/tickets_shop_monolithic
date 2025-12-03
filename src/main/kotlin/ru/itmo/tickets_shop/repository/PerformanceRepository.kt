package ru.itmo.tickets_shop.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.tickets_shop.entity.Performance

interface PerformanceRepository : JpaRepository<Performance, Long> {
    fun findPerformanceById(id: Long): Performance?
}
