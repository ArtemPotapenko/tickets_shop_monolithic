package ru.itmo.tickets_shop.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.pagination")
class PaginationProperties {
    var maxSize: Int = 50
}