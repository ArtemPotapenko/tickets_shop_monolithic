package ru.itmo.tickets_shop.dto

data class TheatreViewDto(
    val id: Long,
    val name: String,
    val city: String,
    val address: String
)

data class TheatreDto(
    val id: Long,
    val name: String,
    val city: String,
    val address: String,
    val halls: List<HallViewDto>,
    val shows : List<ShowViewDto>
)

data class TheatrePayload(
    val id: Long?,
    val name: String,
    val city: String,
    val address: String,
    val halls: List<HallViewDto>
)