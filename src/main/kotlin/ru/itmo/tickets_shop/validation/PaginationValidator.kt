package ru.itmo.tickets_shop.validation

import org.springframework.stereotype.Component
import ru.itmo.tickets_shop.config.PaginationProperties

@Component
class PaginationValidator(
    val props: PaginationProperties
) {
    fun validateSize(size: Int) {
        if (size > props.maxSize) {
            throw IllegalArgumentException("Page size cannot exceed ${props.maxSize}")
        }
    }
}