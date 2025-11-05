package ru.itmo.tickets_shop.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var reservedAt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.RESERVED,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "order_ticket",
        joinColumns = [JoinColumn(name = "order_id")],
        inverseJoinColumns = [JoinColumn(name = "ticket_id")]
    )
    var tickets: MutableList<Ticket> = mutableListOf()
)
