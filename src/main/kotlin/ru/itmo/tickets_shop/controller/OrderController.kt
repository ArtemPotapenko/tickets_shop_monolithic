package ru.itmo.tickets_shop.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import ru.itmo.tickets_shop.dto.*
import ru.itmo.tickets_shop.service.OrderService

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "Order and tickets management APIs")
class OrderController(private val orderService: OrderService) {

    @Operation(summary = "Резервирование билетов")
    @PostMapping("/reserve")
    suspend fun reserveTickets(@RequestBody payload: OrderPayload): OrderDto {
        return orderService.reserveTickets(payload)
    }

    @Operation(summary = "Оплата билетов по заказу")
    @PostMapping("/{orderId}/pay")
    suspend fun payTickets(@PathVariable orderId: Long): OrderDto {
        return orderService.payTickets(orderId)
    }

    @Operation(summary = "Получение билетов по заказу с постраничной прокруткой")
    @GetMapping("/{orderId}/tickets")
    suspend fun getTicketsPage(
        @PathVariable orderId: Long,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 10
    ): PageScrollDto<TicketDto> {
        val pageResult = orderService.getTicketsPageByOrderId(orderId, page, size)
        return PageScrollDto(
            hasNextPage = pageResult.hasNext(),
            content = pageResult.content
        )
    }
}
