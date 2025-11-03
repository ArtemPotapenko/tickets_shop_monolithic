package ru.itmo.tickets_shop.exception

import jakarta.persistence.EntityNotFoundException

class TheatreNotFoundException(message : String) : EntityNotFoundException(message) {
}