package ru.itmo.tickets_shop.entity

import Performance
import jakarta.persistence.Entity

import jakarta.persistence.*
import lombok.EqualsAndHashCode
import lombok.ToString

@Entity
@Table(name = "theatre")
@ToString
@EqualsAndHashCode
class Theatre(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,
    val city: String,
    val address: String,
    @OneToMany(mappedBy = "theatre", cascade = [CascadeType.ALL], orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    val halls: MutableList<Hall> = mutableListOf(),

    @ManyToMany(mappedBy = "theatres")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    val performances: MutableList<Performance> = mutableListOf()
)
