package ru.itmo.tickets_shop.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.itmo.tickets_shop.dto.OrderDto
import ru.itmo.tickets_shop.dto.OrderPayload
import ru.itmo.tickets_shop.dto.TicketDto
import ru.itmo.tickets_shop.entity.OrderStatus
import ru.itmo.tickets_shop.entity.TicketStatus
import ru.itmo.tickets_shop.exception.NotFreeSeatException
import ru.itmo.tickets_shop.exception.OrderNotFoundException
import ru.itmo.tickets_shop.mapper.toDto
import ru.itmo.tickets_shop.mapper.toOrder
import ru.itmo.tickets_shop.mapper.toTicket
import ru.itmo.tickets_shop.repository.OrderRepository
import ru.itmo.tickets_shop.repository.SeatPriceRepository
import ru.itmo.tickets_shop.repository.TicketRepository
import java.time.LocalDateTime

@Service
@Transactional
open class OrderService(
    private val orderRepository: OrderRepository,
    private val seatPriceRepository: SeatPriceRepository,
    private val ticketRepository: TicketRepository
) {

    private val log: Logger = LoggerFactory.getLogger(OrderService::class.java)

    open suspend fun reserveTickets(orderPayload: OrderPayload): OrderDto {
        log.info("Резервирование билетов для шоу id={} и мест {}", orderPayload.showId, orderPayload.seatIds)

        val seatsPrice =
            seatPriceRepository.findSeatsByShowIdAndIdIn(orderPayload.showId, orderPayload.seatIds)
        val tickets = ticketRepository.findAllBySeatIdInAndShowId(
            seatsPrice.map { it.seat.id },
            orderPayload.showId, TicketStatus.CANCELLED
        )
        if (!tickets.isEmpty()) {
            log.warn("Невозможно создать заказ — места заняты: {}", orderPayload.seatIds)
            throw NotFreeSeatException("Невозможно создать заказ -- есть занятые места")
        }

        val ticketsEntity = seatsPrice.map { it.toTicket() }.toMutableList()
        val sumOf = seatsPrice.sumOf { it.price }

        val order = orderPayload.toOrder(ticketsEntity, sumOf)
        order.tickets = ticketsEntity
        orderRepository.save(order)

        log.info("Заказ создан успешно: orderId={}, сумма={}", order.id, sumOf)
        return order.toDto()
    }

    @Scheduled(fixedRate = 60_000)
    open suspend fun cancelExpiredOrders() {
        val now = LocalDateTime.now()
        val expired = orderRepository.findAllByStatusAndReservedAtBefore(OrderStatus.RESERVED, now)

        log.info("Автоотмена заказов. Найдено просроченных заказов: {}", expired.size)

        expired.forEach { order ->
            order.status = OrderStatus.CANCELLED
            order.tickets.forEach { ticket ->
                ticket.status = TicketStatus.CANCELLED
                ticketRepository.save(ticket)
            }
            orderRepository.save(order)
            log.info("Заказ {} автоматически отменён", order.id)
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    open suspend fun payTickets(orderId: Long): OrderDto {
        log.info("Оплата заказа id={}", orderId)

        val order = orderRepository.findOrderById(orderId)
            ?: throw OrderNotFoundException("Заказ не найден: id=$orderId")

        order.tickets = ticketRepository.findAllByOrder(order.id).toMutableList()

        if (order.status == OrderStatus.PAID) {
            log.warn("Заказ {} уже оплачен", orderId)
            throw IllegalStateException("Заказ уже оплачен")
        }

        val now = LocalDateTime.now()
        if (order.reservedAt != null && order.reservedAt!!.isBefore(now)) {
            order.status = OrderStatus.CANCELLED
            order.tickets.forEach {
                it.status = TicketStatus.CANCELLED
                ticketRepository.save(it)
            }
            orderRepository.save(order)
            log.warn("Время оплаты истекло — заказ {} автоматически отменён", orderId)
            throw IllegalStateException("Время оплаты истекло — заказ автоматически отменён")
        }

        order.status = OrderStatus.PAID
        order.tickets.forEach { ticket ->
            ticket.status = TicketStatus.PAID
            ticketRepository.save(ticket)
        }

        val saved = orderRepository.save(order)
        saved.tickets = order.tickets

        log.info("Заказ {} оплачен успешно", orderId)
        return saved.toDto()
    }

    @Transactional(readOnly = true)
    open suspend fun getTicketsPageByOrderId(orderId: Long, page: Int, size: Int): Page<TicketDto> {
        log.info("Получение билетов по заказу id={}, страница={}, размер страницы={}", orderId, page, size)

        if (!orderRepository.existsById(orderId)) {
            log.warn("Заказ {} не найден при получении билетов", orderId)
            throw OrderNotFoundException("Заказ не найден: id=$orderId")
        }

        val pageable = PageRequest.of(page, size)
        val projectionPage = ticketRepository.findTicketsByOrderId(orderId, pageable)

        log.info("Найдено билетов: {} для заказа {}", projectionPage.totalElements, orderId)
        return projectionPage.map {
            TicketDto(
                id = it.id,
                price = it.price,
                raw = it.rowNumber,
                number = it.seatNumber
            )
        }
    }
}
