package ru.itmo.tickets_shop.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.itmo.tickets_shop.dto.*
import ru.itmo.tickets_shop.service.ShowService

@RestController
@RequestMapping("/api/shows")
@Tag(name = "Show", description = "Контроллер для работы с шоу")
class ShowController(
    private val showService: ShowService
) {

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о шоу по id")
    fun getShowInfo(
        @PathVariable id: Long
    ): ResponseEntity<ShowDto> {
        return ResponseEntity.ok(showService.getShowInfo(id))
    }

    @GetMapping
    @Operation(summary = "Получить список шоу в городе (с пагинацией)")
    fun getAllShow(
        @RequestParam city: String,
        @RequestParam page: Int,
        @RequestParam pageSize: Int
    ): ResponseEntity<PageCountDto<ShowViewDto>> {

        val resultPage = showService.getAllShow(city, page, pageSize)

        val dto = PageCountDto(
            pageNumber = resultPage.number + 1,
            pageSize = resultPage.size,
            content = resultPage.content
        )

        return ResponseEntity.ok()
            .header("X-Total-Count", resultPage.totalElements.toString())
            .body(dto)
    }

    @GetMapping("/{id}/seats")
    fun getAllSeatsPaged(
        @PathVariable id: Long,
        @RequestParam page: Int = 1,
        @RequestParam pageSize: Int = 10
    ): ResponseEntity<PageCountDto<SeatRawDto>>{
        val resultPage = showService.getAllSeats(id, page, pageSize)
        val dto = PageCountDto(
            pageNumber = resultPage.number + 1,
            pageSize = resultPage.size,
            content = resultPage.content
        )
        return ResponseEntity.ok()
            .header("X-Total-Count", resultPage.totalElements.toString())
            .body(dto)
    }

    @PostMapping("/performance")
    @Operation(summary = "Создать Performance")
    fun createPerformance(
        @RequestBody dto: PerformanceDto
    ): ResponseEntity<PerformanceDto> {
        return ResponseEntity.ok(showService.createPerformance(dto))
    }

    @PutMapping("/performance/{id}")
    @Operation(summary = "Обновить Performance")
    fun updatePerformance(
        @PathVariable id: Long,
        @RequestBody dto: PerformanceDto
    ): ResponseEntity<PerformanceDto> {
        return ResponseEntity.ok(showService.updatePerformance(id, dto))
    }
}
