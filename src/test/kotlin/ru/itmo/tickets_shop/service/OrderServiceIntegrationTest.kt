package ru.itmo.tickets_shop

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
import ru.itmo.tickets_shop.exception.NotFreeSeatException
import ru.itmo.tickets_shop.exception.OrderNotFoundException
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
        scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun reserveTicketsSuccess() {
        val payload = OrderPayload(showId = 1L, seatIds = listOf(3L))
        val dto = orderService.reserveTickets(payload)

        assertNotNull(dto.id)
        assertEquals(OrderStatus.RESERVED, dto.status)
        assertEquals(900, dto.price)
    }

    @Test
    @DisplayName("Резервирование — ошибка, если место занято")
    @Sql(
        scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun reserveTicketsFailIfOccupied() {
        val payload = OrderPayload(showId = 1L, seatIds = listOf(1L)) // место 1 занято
        assertThrows<NotFreeSeatException> {
            orderService.reserveTickets(payload)
        }
    }

    @Test
    @DisplayName("Автоотмена заказов — RESERVED + reserved_at < now")
    @Sql(
        scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun cancelExpiredOrders() {
        val order = orderRepository.findById(1L).get()
        order.reservedAt = LocalDateTime.now().minusMinutes(10)
        orderRepository.save(order)

        orderService.cancelExpiredOrders()

        val updated = orderRepository.findById(1L).get()
        assertEquals(OrderStatus.CANCELLED, updated.status)
    }

    @Test
    @DisplayName("Автоотмена заказов — нет просроченных")
    @Sql(
        scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun cancelExpiredOrdersNoExpired() {
        orderService.cancelExpiredOrders() // должно пройти без ошибок
    }

    @Test
    @DisplayName("Оплата билета — успешный кейс")
    @Sql(
        scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun payTicketsSuccess() {
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
        scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun payTicketsExpired() {
        val order = orderRepository.findById(1L).get()
        order.reservedAt = LocalDateTime.now().minusMinutes(5)
        orderRepository.save(order)

        assertThrows<IllegalStateException> {
            orderService.payTickets(1L)
        }

        orderService.cancelExpiredOrders()
        val updated = orderRepository.findById(1L).get()
        assertEquals(OrderStatus.CANCELLED, updated.status)
    }

    @Test
    @DisplayName("Оплата билета — ошибка, если заказ уже оплачен")
    @Sql(
        scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun payTicketsAlreadyPaid() {
        val order = orderRepository.findById(2L).get()
        assertEquals(OrderStatus.PAID, order.status)

        assertThrows<IllegalStateException> {
            orderService.payTickets(2L)
        }
    }

    @Test
    @DisplayName("Оплата билета — ошибка, если заказ не найден")
    fun payTicketsNotFound() {
        assertThrows<OrderNotFoundException> {
            orderService.payTickets(999L)
        }
    }

    @Test
    @DisplayName("Получение Page<TicketDto> по orderId — успешный кейс")
    @Sql(
        scripts = ["classpath:sql/clean.sql","classpath:sql/init.sql","classpath:sql/insert.sql"],
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    fun getTicketsPageSuccess() {
        val page = orderService.getTicketsPageByOrderId(1L, 0, 10)

        assertEquals(1, page.totalElements)
        val dto = page.content.first()
        assertEquals(1L, dto.id)
        assertEquals(1000, dto.price)
        assertEquals(1, dto.raw)
        assertEquals(1, dto.number)
    }

    @Test
    @DisplayName("Получение билетов — ошибка, если заказ не найден")
    fun getTicketsPageOrderNotFound() {
        assertThrows<OrderNotFoundException> {
            orderService.getTicketsPageByOrderId(999L, 0, 10)
        }
    }
}
