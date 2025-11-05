package ru.itmo.tickets_shop.entity

import jakarta.persistence.*


@Entity
@Table(name = "theatre")
class Theatre(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,
    val city: String,
    val address: String,
    @OneToMany(mappedBy = "theatre", cascade = [CascadeType.ALL], orphanRemoval = true)
    var halls: MutableList<Hall> = mutableListOf(),

    @ManyToMany(mappedBy = "theatres")
    var performances: MutableList<Performance> = mutableListOf()
)
