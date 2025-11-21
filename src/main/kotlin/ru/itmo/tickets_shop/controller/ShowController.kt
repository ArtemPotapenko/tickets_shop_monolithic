package ru.itmo.tickets_shop.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.itmo.tickets_shop.dto.PageCountDto
import ru.itmo.tickets_shop.dto.SeatRawDto
import ru.itmo.tickets_shop.dto.ShowDto
import ru.itmo.tickets_shop.dto.ShowViewDto
import ru.itmo.tickets_shop.service.ShowService

@RestController
@RequestMapping("/api/v1/show")
@Tag(name = "Show", description = "Контроллер для работы с шоу")
class ShowController(
    private val showService: ShowService
) {

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о шоу по id")
    suspend fun getShowInfo(
        @Parameter(description = "ID шоу")
        @PathVariable id: Long
    ): ResponseEntity<ShowDto> {
        return ResponseEntity.ok(showService.getShowInfo(id))
    }

    @GetMapping
    @Operation(summary = "Получить список шоу в городе (с пагинацией)")
    suspend fun getAllShow(
        @Parameter(description = "Город") @RequestParam city: String,
        @Parameter(description = "Страница (нумерация начинается с 1)") @RequestParam page: Int,
        @Parameter(description = "Размер страницы") @RequestParam pageSize: Int
    ): ResponseEntity<PageCountDto<ShowViewDto>> {

        val resultPage = showService.getAllShow(city, page, pageSize)

        val dto = PageCountDto(
            pageNumber = resultPage.number + 1,
            pageSize = resultPage.size,
            content = resultPage.content
        )

        return ResponseEntity.ok(dto)
    }

    @GetMapping("/{id}/seats")
    @Operation(summary = "Получить список мест для шоу")
    suspend fun getAllSeats(
        @Parameter(description = "ID шоу")
        @PathVariable id: Long
    ): ResponseEntity<List<SeatRawDto>> {
        return ResponseEntity.ok(showService.getAllSeats(id))
    }
}
