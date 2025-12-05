package ru.itmo.tickets_shop

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import ru.itmo.tickets_shop.dto.PerformanceDto
import ru.itmo.tickets_shop.exception.ShowNotFoundException
import ru.itmo.tickets_shop.service.ShowService

@SpringBootTest
open class ShowServiceIntegrationTest : PostgresContainerConfig() {

    @Autowired
    private lateinit var showService: ShowService

    @Test
    @DisplayName("Получение информации о шоу — успешный кейс")
    @Sql(scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun getShowInfoSuccess() {
        val dto = showService.getShowInfo(1L)
        assertEquals(1L, dto.id)
        assertEquals(120, dto.durationMinutes)
        assertNotNull(dto.hall)
    }

    @Test
    @DisplayName("Получение информации о шоу — ошибка, если шоу не найдено")
    @Sql(scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun getShowInfoNotFound() {
        assertThrows<ShowNotFoundException> {
            showService.getShowInfo(999L)
        }
    }

    @Test
    @DisplayName("Получение всех шоу в городе")
    @Sql(scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun getAllShowInCity() {
        val page = showService.getAllShow("Москва", 1, 10)
        assertTrue(page.content.isNotEmpty())
        assertEquals("Лебединое озеро", page.content.first().tittle)
    }

    @Test
    @DisplayName("Получение всех мест на шоу")
    @Sql(scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun getAllSeatsForShow() {
        val seats = showService.getAllSeats(1L)
        assertTrue(seats.isNotEmpty())
        assertTrue(seats.first().seats.isNotEmpty())
        assertTrue(seats.any { it.row > 0 })
    }

    @Test
    @DisplayName("Создание нового Performance")
    @Sql(scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun createPerformance() {
        val dto = PerformanceDto(null, "Новая постановка", "Описание новой постановки", 90)
        val saved = showService.createPerformance(dto)

        assertNotNull(saved.id)
        assertEquals("Новая постановка", saved.title)
        assertEquals("Описание новой постановки", saved.description)
        assertEquals(90, saved.durationMinutes)
    }

    @Test
    @DisplayName("Обновление существующего Performance")
    @Sql(scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun updatePerformance() {
        val dto = PerformanceDto(null, "Обновлённое название", "Обновлённое описание", 150)
        val updated = showService.updatePerformance(1L, dto)

        assertEquals(1L, updated.id)
        assertEquals("Обновлённое название", updated.title)
        assertEquals("Обновлённое описание", updated.description)
        assertEquals(150, updated.durationMinutes)
    }

    @Test
    @DisplayName("Обновление Performance — ошибка, если не найдено")
    @Sql(scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun updatePerformanceNotFound() {
        val dto = PerformanceDto(null, "Тест", "Описание", 100)
        assertThrows<RuntimeException> {
            showService.updatePerformance(999L, dto)
        }
    }

    @Test
    @DisplayName("Получение всех шоу — пустой город")
    @Sql(scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun getAllShowEmptyCity() {
        val page = showService.getAllShow("Неизвестный город", 1, 10)
        assertTrue(page.content.isEmpty())
    }
}
