package ru.itmo.tickets_shop.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import ru.itmo.tickets_shop.dto.TheatreDto
import ru.itmo.tickets_shop.dto.TheatrePayload
import ru.itmo.tickets_shop.dto.TheatreViewDto
import ru.itmo.tickets_shop.dto.PageCountDto
import ru.itmo.tickets_shop.service.TheatreService

@RestController
@RequestMapping("/api/theatres")
@Tag(name = "Theatre", description = "Theatre management APIs")
class TheatreController(private val theatreService: TheatreService) {

    @Operation(summary = "Получить список театров в городе")
    @GetMapping
    suspend fun getAllTheatres(
        @RequestParam city: String,
        @RequestParam page: Int = 1,
        @RequestParam size: Int = 10
    ): PageCountDto<TheatreViewDto> {
        val result = theatreService.getAllTheatreInCity(city, page, size)
        return PageCountDto(
            pageNumber = page,
            pageSize = size,
            content = result.content
        )
    }

    @Operation(summary = "Получить информацию о театре по ID")
    @GetMapping("/{id}")
    suspend fun getTheatreInfo(@PathVariable id: Long): TheatreDto {
        return theatreService.getTheatreInfo(id)
    }

    @Operation(summary = "Создать театр")
    @PostMapping
    suspend fun createTheatre(@RequestBody payload: TheatrePayload): TheatrePayload {
        return theatreService.createTheatre(payload)
    }

    @Operation(summary = "Обновить театр")
    @PutMapping
    suspend fun updateTheatre(@RequestBody payload: TheatrePayload): TheatrePayload {
        return theatreService.updateTheatre(payload)
    }
}
