package ru.itmo.tickets_shop

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import ru.itmo.tickets_shop.dto.HallViewDto
import ru.itmo.tickets_shop.dto.TheatrePayload
import ru.itmo.tickets_shop.exception.TheatreNotFoundException
import ru.itmo.tickets_shop.service.TheatreService

@SpringBootTest
open class TheatreServiceIntegrationTest : PostgresContainerConfig() {

    @Autowired
    private lateinit var theatreService: TheatreService

    @Test
    @DisplayName("Создание театра с залами")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun createTheatreWithHalls() {
        val halls = listOf(
            HallViewDto(number = 1, id = null),
            HallViewDto(number = 2, id = null)
        )

        val payload = TheatrePayload(
            id = null,
            name = "Новый театр",
            city = "Санкт-Петербург",
            address = "Невский проспект 1",
            halls = halls
        )

        val created = runBlocking { theatreService.createTheatre(payload) }

        assertNotNull(created.id)
        assertEquals(2, created.halls.size)
        assertEquals("Новый театр", created.name)
    }

    @Test
    @DisplayName("Обновление театра — ошибка, если id = null")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun updateTheatreThrowsWhenIdNull() {
        val payload = TheatrePayload(
            id = null,
            name = "Театр без id",
            city = "Москва",
            address = "Красная площадь 1",
            halls = emptyList()
        )

        val ex = assertThrows<IllegalArgumentException> {
            runBlocking { theatreService.updateTheatre(payload) }
        }

        assertEquals("ID должен быть не null при обновлении", ex.message)
    }

    @Test
    @DisplayName("Обновление театра — исключение для несуществующего id")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun updateTheatreThrowsWhenIdNotFound() {
        val payload = TheatrePayload(
            id = 999L,
            name = "Несуществующий театр",
            city = "Москва",
            address = "ул. Пушкина, дом Колотушкина",
            halls = emptyList()
        )

        assertThrows<TheatreNotFoundException> {
            runBlocking { theatreService.updateTheatre(payload) }
        }
    }

    @Test
    @DisplayName("Успешное обновление театра")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun updateTheatreSuccess() {
        val updatePayload = TheatrePayload(
            id = 1L,
            name = "Обновлённый театр",
            city = "Москва",
            address = "Новая улица 5",
            halls = listOf(
                HallViewDto(id = 1L, number = 10),
                HallViewDto(id = 2L, number = 20)
            )
        )

        val updated = runBlocking { theatreService.updateTheatre(updatePayload) }

        assertEquals(1L, updated.id)
        assertEquals("Обновлённый театр", updated.name)
        assertEquals("Москва", updated.city)
        assertEquals(2, updated.halls.size)
        assertEquals(10, updated.halls[0].number)
    }

    @Test
    @DisplayName("Получение информации о театре")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getTheatreInfoSuccess() {
        val theatreId = 1L

        val theatre = runBlocking { theatreService.getTheatreInfo(theatreId) }

        assertEquals("Мариинский театр", theatre.name)
        assertTrue(theatre.halls.isNotEmpty())
    }

    @Test
    @DisplayName("Получение театра — исключение, если id не существует")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getTheatreInfoThrowsWhenNotFound() {
        assertThrows<TheatreNotFoundException> {
            runBlocking { theatreService.getTheatreInfo(999L) }
        }
    }

    @Test
    @DisplayName("Получение всех театров в городе")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getAllTheatresInCity() {
        val page = runBlocking { theatreService.getAllTheatreInCity("Москва", 1, 10) }

        assertTrue(page.content.isNotEmpty())
        assertEquals("Москва", page.content.first().city)
    }
}
