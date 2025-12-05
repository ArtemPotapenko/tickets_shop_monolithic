package ru.itmo.tickets_shop.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.itmo.tickets_shop.dto.*
import ru.itmo.tickets_shop.service.TheatreService
import ru.itmo.tickets_shop.validation.PaginationValidator

@WebMvcTest(TheatreController::class)
class TheatreControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var theatreService: TheatreService

    @MockkBean
    lateinit var paginationValidator: PaginationValidator

    // GET /api/theatres (200 - OK)
    @Test
    fun `getAllTheatres - success`() {
        every { paginationValidator.validateSize(any()) } just runs

        val dto = TheatreViewDto(
            id = 1L,
            name = "Мариинский",
            city = "Москва",
            address = "Невский 1"
        )

        val page = PageImpl(listOf(dto), PageRequest.of(0, 10), 1)

        every { theatreService.getAllTheatreInCity("Москва", 1, 10) } returns page

        mockMvc.perform(
            get("/api/theatres")
                .param("city", "Москва")
                .param("page", "1")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(header().string("X-Total-Count", "1"))
            .andExpect(header().string("X-Page-Number", "1"))
            .andExpect(header().string("X-Page-Size", "10"))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Мариинский"))
    }

    // GET /api/theatres/{id} (200 - OK)
    @Test
    fun `getTheatreInfo - success`() {
        val dto = TheatreDto(
            id = 1L,
            name = "Мариинский",
            city = "СПб",
            address = "Мойка 1",
            halls = listOf(HallViewDto(1L, 1)),
            shows = emptyList()
        )

        every { theatreService.getTheatreInfo(1L) } returns dto

        mockMvc.perform(get("/api/theatres/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.halls.length()").value(1))
    }

    // POST /api/theatres (200 - OK)
    @Test
    fun `createTheatre - success`() {
        val payload = TheatrePayload(
            id = null,
            name = "Новый",
            city = "Москва",
            address = "Ленина 1",
            halls = listOf(HallViewDto(null, 1))
        )

        every { theatreService.createTheatre(any()) } returns payload.copy(id = 10L)

        mockMvc.perform(
            post("/api/theatres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": null,
                      "name": "Новый",
                      "city": "Москва",
                      "address": "Ленина 1",
                      "halls": [{"id": null, "number": 1}]
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.name").value("Новый"))
    }

    // PUT /api/theatres (200 - OK)
    @Test
    fun `updateTheatre - success`() {
        val payload = TheatrePayload(
            id = 1L,
            name = "Обновленный",
            city = "Москва",
            address = "Ленина 2",
            halls = listOf(HallViewDto(1L, 2))
        )

        every { theatreService.updateTheatre(any()) } returns payload

        mockMvc.perform(
            put("/api/theatres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": 1,
                      "name": "Обновленный",
                      "city": "Москва",
                      "address": "Ленина 2",
                      "halls": [{"id": 1, "number": 2}]
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Обновленный"))
    }

    // POST /api/theatres/{theatreId}/halls (200 - OK)
    @Test
    fun `createHall - success`() {
        val dto = HallDto(
            id = 5L,
            number = 3,
            seatRows = emptyList()
        )

        every { theatreService.createHall(1L, any()) } returns dto

        mockMvc.perform(
            post("/api/theatres/1/halls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": null,
                      "number": 3,
                      "seatRows": []
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.number").value(3))
    }

    // PUT /api/theatres/halls/{id} (200 - OK)
    @Test
    fun `updateHall - success`() {
        val dto = HallDto(
            id = 10L,
            number = 99,
            seatRows = emptyList()
        )

        every { theatreService.updateHall(10L, any()) } returns dto

        mockMvc.perform(
            put("/api/theatres/halls/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": null,
                      "number": 99,
                      "seatRows": []
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.number").value(99))
    }

    // DELETE /api/theatres/halls/{id} (200 - OK)
    @Test
    fun `deleteHall - success`() {
        every { theatreService.deleteHall(10L) } just runs

        mockMvc.perform(delete("/api/theatres/halls/10"))
            .andExpect(status().isOk)
    }
}
