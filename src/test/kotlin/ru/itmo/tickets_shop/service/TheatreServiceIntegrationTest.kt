package ru.itmo.tickets_shop

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import ru.itmo.tickets_shop.dto.*
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
            HallViewDto(id = null, number = 1),
            HallViewDto(id = null, number = 2)
        )

        val payload = TheatrePayload(
            id = null,
            name = "Новый театр",
            city = "Санкт-Петербург",
            address = "Невский проспект 1",
            halls = halls
        )

        val created = theatreService.createTheatre(payload)

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
            theatreService.updateTheatre(payload)
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
            theatreService.updateTheatre(payload)
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

        val updated = theatreService.updateTheatre(updatePayload)

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

        val theatre = theatreService.getTheatreInfo(theatreId)

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
            theatreService.getTheatreInfo(999L)
        }
    }

    @Test
    @DisplayName("Получение всех театров в городе")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getAllTheatresInCity() {
        val page = theatreService.getAllTheatreInCity("Москва", 1, 10)

        assertTrue(page.content.isNotEmpty())
        assertEquals("Москва", page.content.first().city)
    }

    // 🟢 Тесты на залы
    @Test
    @DisplayName("Создание зала для театра с SeatRawDto")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun createHallWithSeats() {
        val seatRows = listOf(
            SeatRawDto(
                row = 1,
                seats = listOf(
                    SeatStatusDto(id = 0L, status = SeatStatus.FREE, number = 1, price = 1000),
                    SeatStatusDto(id = 0L, status = SeatStatus.FREE, number = 2, price = 1200)
                )
            )
        )

        val hallDto = HallDto(id = null, number = 5, seatRows = seatRows)

        val created = theatreService.createHall(1L, hallDto)

        assertNotNull(created.id)
        assertEquals(5, created.number)
        assertEquals(1, created.seatRows.size)
        assertEquals(2, created.seatRows[0].seats.size)
    }

    @Test
    @DisplayName("Обновление зала с SeatRawDto")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun updateHallWithSeats() {
        val seatRows = listOf(
            SeatRawDto(
                row = 1,
                seats = listOf(
                    SeatStatusDto(id = 0L, status = SeatStatus.FREE, number = 1, price = 1000)
                )
            )
        )

        val hallDto = HallDto(id = null, number = 10, seatRows = seatRows)

        val updated = theatreService.updateHall(1L, hallDto)

        assertEquals(10, updated.number)
        assertEquals(1, updated.seatRows.size)
        assertEquals(1, updated.seatRows[0].seats.size)
    }

    @Test
    @DisplayName("Удаление зала")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun deleteHall() {
        theatreService.deleteHall(1L)
        assertThrows<RuntimeException> { theatreService.deleteHall(1L) }
    }

    @Test
    @DisplayName("Удаление несуществующего зала")
    @Sql(
        scripts = ["classpath:sql/clean.sql", "classpath:sql/init.sql", "classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun deleteHallNotFound() {
        assertThrows<RuntimeException> { theatreService.deleteHall(999L) }
    }
}
