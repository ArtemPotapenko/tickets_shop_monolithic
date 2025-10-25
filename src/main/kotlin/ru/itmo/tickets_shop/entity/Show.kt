import jakarta.persistence.*
import ru.itmo.tickets_shop.entity.Hall
import sun.security.krb5.internal.Ticket
import java.time.LocalDateTime

@Entity
@Table(name = "show")
class Show(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    val performance: Performance,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    val hall: Hall,

    val showTime: LocalDateTime,

    @OneToMany(mappedBy = "show")
    val seatPrices: List<SeatPrice> = mutableListOf(),

    @OneToMany(mappedBy = "show")
    val tickets: List<Ticket> = mutableListOf()
)
