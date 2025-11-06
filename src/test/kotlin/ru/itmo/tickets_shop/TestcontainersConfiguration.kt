package ru.itmo.tickets_shop

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

abstract class PostgresContainerConfig {
    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withDatabaseName("tickets_shop_test")
            withUsername("test")
            withPassword("test")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }
    }
}