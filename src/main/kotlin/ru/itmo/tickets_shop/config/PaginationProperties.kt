package ru.itmo.tickets_shop.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.pagination")
open class PaginationProperties {
    var maxSize: Int = 50
}