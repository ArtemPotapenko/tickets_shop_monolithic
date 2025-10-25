import jakarta.persistence.*
import lombok.EqualsAndHashCode
import lombok.ToString
import ru.itmo.tickets_shop.entity.Theatre

@Entity
@Table(name = "performance")
@ToString
@EqualsAndHashCode
class Performance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var title: String,
    var description: String? = null,
    var durationMinutes: Int? = null,

    @OneToMany(mappedBy = "performance")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    val shows: MutableList<Show> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "theatre_performance",
        joinColumns = [JoinColumn(name = "performance_id")],
        inverseJoinColumns = [JoinColumn(name = "theatre_id")]
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    val theatres: MutableList<Theatre> = mutableListOf()
)
