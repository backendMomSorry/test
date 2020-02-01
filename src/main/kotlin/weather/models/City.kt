package weather.models

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

const val DEFAULT_INTERVAL = 30_000L

@Entity
@Table(name = "cities")
class City {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "tracking")
    var tracking: Boolean = true

    @Column(name = "interval")
    var interval: Long = DEFAULT_INTERVAL

}
