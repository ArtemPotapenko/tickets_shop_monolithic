package ru.itmo.tickets_shop.entity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var reservedAt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.RESERVED,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val tickets: MutableList<Ticket> = mutableListOf()
)
