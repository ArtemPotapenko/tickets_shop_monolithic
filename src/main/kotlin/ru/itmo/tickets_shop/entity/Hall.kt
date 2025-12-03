package ru.itmo.tickets_shop.entity

import jakarta.persistence.*

@Entity
@Table(name = "hall")
class Hall(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var number: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id")
    val theatre: Theatre?,

    @OneToMany(mappedBy = "hall", cascade = [CascadeType.ALL], orphanRemoval = true)
    var seats: MutableList<Seat> = mutableListOf()
)
{
    constructor() : this(0, 0, null)
}