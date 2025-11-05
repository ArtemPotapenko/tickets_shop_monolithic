package ru.itmo.tickets_shop.entity

import jakarta.persistence.*

@Entity
@Table(name = "hall")
class Hall(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val number: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id")
    val theatre: Theatre?,

    @OneToMany(mappedBy = "hall", cascade = [CascadeType.ALL], orphanRemoval = true)
    var seats: MutableList<Seat> = mutableListOf()
)

