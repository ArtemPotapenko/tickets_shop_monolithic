package ru.itmo.tickets_shop.dto

data class PageCountDto<T>(val pageNumber: Int, val pageSize: Int, val content: List<T>)

data class PageScrollDto<T>(val hasNextPage: Boolean, val content: List<T>)

