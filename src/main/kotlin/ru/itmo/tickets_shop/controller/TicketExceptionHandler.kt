package ru.itmo.tickets_shop.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.NoHandlerFoundException
import ru.itmo.tickets_shop.entity.ErrorResponse
import ru.itmo.tickets_shop.exception.NotFreeSeatException
import ru.itmo.tickets_shop.exception.OrderNotFoundException
import ru.itmo.tickets_shop.exception.ShowNotFoundException
import ru.itmo.tickets_shop.exception.TheatreNotFoundException

@ControllerAdvice
open class TicketExceptionHandler {
    @ExceptionHandler(TheatreNotFoundException::class, ShowNotFoundException::class, OrderNotFoundException::class)
    fun handleNotFound(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Not Found "), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(NotFreeSeatException::class)
    fun handleNotFreeSeat(ex: NotFreeSeatException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Seat not free"), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(IllegalStateException::class, IllegalArgumentException::class)
    fun handleBadRequest(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Bad request"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandler(ex: NoHandlerFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse("Endpoint not found: ${ex.requestURL}"),
            HttpStatus.NOT_FOUND
        )

    // ❗ неправильный HTTP метод
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse("Method not allowed: ${ex.method}"),
            HttpStatus.METHOD_NOT_ALLOWED
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleBadJson(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse("Invalid JSON or request body"),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse("Internal server error"),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
}