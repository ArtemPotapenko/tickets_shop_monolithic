package ru.itmo.tickets_shop.exception

import jakarta.persistence.EntityNotFoundException

class ShowNotFoundException(message : String) : EntityNotFoundException(message) {
}