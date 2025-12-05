package ru.itmo.tickets_shop.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.itmo.tickets_shop.config.PaginationProperties
import ru.itmo.tickets_shop.dto.HallViewDto
import ru.itmo.tickets_shop.dto.ShowDto
import ru.itmo.tickets_shop.dto.ShowViewDto
import ru.itmo.tickets_shop.dto.TheatreViewDto
import ru.itmo.tickets_shop.exception.ShowNotFoundException
import ru.itmo.tickets_shop.service.ShowService
import ru.itmo.tickets_shop.validation.PaginationValidator
import java.time.LocalDateTime

@WebMvcTest(ShowController::class)
class ShowControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var showService: ShowService

    @MockkBean
    private lateinit var paginationValidator: PaginationValidator

    @BeforeEach
    fun setup() {
        every { paginationValidator.props } returns PaginationProperties()
        every { paginationValidator.validateSize(any()) } just Runs
    }

    @Test
    fun `getAllShow returns page successfully`() {
        val show = ShowViewDto(id = 1L, tittle = "Лебединое озеро", date = java.time.LocalDateTime.now())
        val page = PageImpl(listOf(show), PageRequest.of(0, 10), 1)

        every {showService.getAllShow("Moscow", 1, 10)} returns page

        mockMvc.perform(get("/api/shows")
            .param("city", "Moscow")
            .param("page", "1")
            .param("pageSize", "10")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(header().string("X-Total-Count", "1"))
            .andExpect(header().string("X-Page-Size", "10"))
            .andExpect(header().string("X-Page-Number", "1"))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].tittle").value("Лебединое озеро"))
    }

    @Test
    fun `getAllShow fails when pageSize exceeds max`() {
        every {paginationValidator.validateSize(60)} throws IllegalArgumentException("Page size cannot exceed 50")

        mockMvc.perform(get("/api/shows")
            .param("city", "Moscow")
            .param("page", "1")
            .param("pageSize", "60")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest)
//            .andExpect(jsonPath("$.message").value("Page size cannot exceed 50"))
    }

    @Test
    fun `getShowInfo returns show`() {
        val show = ShowDto(
            id = 1,
            title = "Лебединое озеро",
            description = "",
            date = LocalDateTime.now(),
            durationMinutes = null,
            hall = HallViewDto(id = 1, number = 1),
            theatre = TheatreViewDto(id = 1, name = "Театр", city = "Moscow", address = "Театральная пл., 1")
        )

        every { showService.getShowInfo(1L) } returns show

        mockMvc.perform(
            get("/api/shows/1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Лебединое озеро"))
    }

    @Test
    fun `getShowInfo throws ShowNotFoundException`() {
        every {showService.getShowInfo(999L)} throws ShowNotFoundException("Show not found")

        mockMvc.perform(get("/api/shows/999")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
//            .andExpect(jsonPath("$.message").value("Show not found"))
    }
}
