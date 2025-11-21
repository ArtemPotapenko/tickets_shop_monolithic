package ru.itmo.tickets_shop.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.itmo.tickets_shop.entity.ErrorResponse
import ru.itmo.tickets_shop.exception.NotFreeSeatException
import ru.itmo.tickets_shop.exception.OrderNotFoundException
import ru.itmo.tickets_shop.exception.ShowNotFoundException
import ru.itmo.tickets_shop.exception.TheatreNotFoundException

class TicketExceptionHandler {
    @ExceptionHandler(TheatreNotFoundException::class, ShowNotFoundException::class, OrderNotFoundException::class)
    fun handleNotFound(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Not Found"), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(NotFreeSeatException::class)
    fun handleNotFreeSeat(ex: NotFreeSeatException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Seat not free"), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(IllegalStateException::class, IllegalArgumentException::class)
    fun handleBadRequest(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Bad request"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(ex.message ?: "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}