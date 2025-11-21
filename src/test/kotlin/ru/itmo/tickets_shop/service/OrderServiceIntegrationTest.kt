package ru.itmo.tickets_shop

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import ru.itmo.tickets_shop.dto.OrderPayload
import ru.itmo.tickets_shop.entity.OrderStatus
import ru.itmo.tickets_shop.entity.TicketStatus
import ru.itmo.tickets_shop.repository.OrderRepository
import ru.itmo.tickets_shop.repository.TicketRepository
import ru.itmo.tickets_shop.service.OrderService
import java.time.LocalDateTime

@SpringBootTest
open class OrderServiceIntegrationTest : PostgresContainerConfig() {

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var ticketRepository: TicketRepository

    @Test
    @DisplayName("Резервирование билетов — успешный кейс")
    @Sql(
        scripts = [
            "classpath:sql/clean.sql",
            "classpath:sql/init.sql",
            "classpath:sql/insert.sql"
        ],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun reserveTicketsSuccess() = runBlocking {
        val payload = OrderPayload(
            showId = 1L,
            seatIds = listOf(3L)
        )

        val dto = orderService.reserveTickets(payload)

        assertNotNull(dto.id)
        assertEquals(OrderStatus.RESERVED, dto.status)
        assertEquals(900, dto.price)
    }

    @Test
    @DisplayName("Автоотмена заказов — RESERVED + reserved_at < now")
    @Sql(
        scripts = [
            "classpath:sql/clean.sql",
            "classpath:sql/init.sql",
            "classpath:sql/insert.sql"
        ],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun cancelExpiredOrders() = runBlocking {
        val order = orderRepository.findById(1L).get()
        order.reservedAt = LocalDateTime.now().minusMinutes(10)
        orderRepository.save(order)

        orderService.cancelExpiredOrders()
    }

    @Test
    @DisplayName("Оплата билета — успешный кейс")
    @Sql(
        scripts = [
            "classpath:sql/clean.sql",
            "classpath:sql/init.sql",
            "classpath:sql/insert.sql"
        ],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun payTicketsSuccess() = runBlocking {
        val order = orderRepository.findById(1L).get()
        order.reservedAt = LocalDateTime.now().plusMinutes(10)
        orderRepository.save(order)

        val dto = orderService.payTickets(1L)

        assertEquals(OrderStatus.PAID, dto.status)
        val updatedTickets = ticketRepository.findAll()
        assertTrue(updatedTickets.any { it.status == TicketStatus.PAID })
    }

    @Test
    @DisplayName("Оплата билета — ошибка, если заказ просрочен")
    @Sql(
        scripts = [
            "classpath:sql/clean.sql",
            "classpath:sql/init.sql",
            "classpath:sql/insert.sql"
        ],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun payTicketsExpired() = runBlocking {
        val order = orderRepository.findById(1L).get()
        order.reservedAt = LocalDateTime.now().minusMinutes(5)
        orderRepository.save(order)

        assertThrows<IllegalStateException> {
            runBlocking { orderService.payTickets(1L) }
        }

        val updated = orderRepository.findById(1L).get()
        assertEquals(OrderStatus.CANCELLED, updated.status)
    }

    @Test
    @DisplayName("Получение Page<TicketDto> по orderId — успешный кейс")
    @Sql(
        scripts = [
            "classpath:sql/clean.sql",
            "classpath:sql/init.sql",
            "classpath:sql/insert.sql"
        ],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getTicketsPageSuccess() = runBlocking {
        val page = orderService.getTicketsPageByOrderId(1L, 0, 10)

        assertEquals(1, page.totalElements)
        val dto = page.content.first()
        assertEquals(1L, dto.id)
        assertEquals(1000, dto.price)
        assertEquals(1, dto.raw)
        assertEquals(1, dto.number)
    }
}
