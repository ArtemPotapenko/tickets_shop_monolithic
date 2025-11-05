package ru.itmo.tickets_shop.entity

import jakarta.persistence.*
import ru.itmo.tickets_shop.entity.Theatre

@Entity
@Table(name = "performance")
class Performance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var title: String,
    var description: String? = null,
    var durationMinutes: Int? = null,

    @OneToMany(mappedBy = "performance")
    val shows: MutableList<Show> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "theatre_performance",
        joinColumns = [JoinColumn(name = "performance_id")],
        inverseJoinColumns = [JoinColumn(name = "theatre_id")]
    )
    var theatres: MutableList<Theatre> = mutableListOf()
)
