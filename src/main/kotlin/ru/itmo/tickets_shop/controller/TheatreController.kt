package ru.itmo.tickets_shop.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.itmo.tickets_shop.dto.*
import ru.itmo.tickets_shop.service.TheatreService
import ru.itmo.tickets_shop.validation.PaginationValidator

@RestController
@RequestMapping("/api/theatres")
@Tag(name = "Theatre", description = "Theatre management APIs")
class TheatreController(
    private val theatreService: TheatreService,
    private val paginationValidator: PaginationValidator,
) {

    @Operation(summary = "Получить список театров в городе")
    @GetMapping
    fun getAllTheatres(
        @RequestParam city: String,
        @RequestParam page: Int = 1,
        @RequestParam size: Int = 10
    ): ResponseEntity<PageCountDto<TheatreViewDto>> {
        paginationValidator.validateSize(size)

        val resultPage = theatreService.getAllTheatreInCity(city, page, size)
        val dto = PageCountDto(
            pageNumber = page,
            pageSize = size,
            content = resultPage.content
        )

        return ResponseEntity.ok()
            .header("X-Total-Count", resultPage.totalElements.toString())
            .header("X-Page-Number", (resultPage.number + 1).toString())
            .header("X-Page-Size", resultPage.size.toString())
            .body(dto)
    }

    @Operation(summary = "Получить информацию о театре по ID")
    @GetMapping("/{id}")
    fun getTheatreInfo(@PathVariable id: Long): TheatreDto {
        return theatreService.getTheatreInfo(id)
    }

    @Operation(summary = "Создать театр")
    @PostMapping
    fun createTheatre(@RequestBody payload: TheatrePayload): TheatrePayload {
        return theatreService.createTheatre(payload)
    }

    @Operation(summary = "Обновить театр")
    @PutMapping
    fun updateTheatre(@RequestBody payload: TheatrePayload): TheatrePayload {
        return theatreService.updateTheatre(payload)
    }

    @Operation(summary = "Создать зал для театра")
    @PostMapping("/{theatreId}/halls")
    fun createHall(@PathVariable theatreId: Long, @RequestBody dto: HallDto): HallDto {
        return theatreService.createHall(theatreId, dto)
    }

    @Operation(summary = "Обновить зал")
    @PutMapping("/halls/{id}")
    fun updateHall(@PathVariable id: Long, @RequestBody dto: HallDto): HallDto {
        return theatreService.updateHall(id, dto)
    }

    @Operation(summary = "Удалить зал")
    @DeleteMapping("/halls/{id}")
    fun deleteHall(@PathVariable id: Long) {
        theatreService.deleteHall(id)
    }
}
