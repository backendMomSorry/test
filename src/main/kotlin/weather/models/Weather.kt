package weather.models

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.Table

@Entity
@Table(name = "weathers")
class Weather {

    @Id
    @Column(name = "city_id")
    var id: Long? = null

    @OneToOne
    @PrimaryKeyJoinColumn(name = "city_id")
    lateinit var city: City

    @Column(name = "main")
    lateinit var main: String

    @Column(name = "description")
    lateinit var description: String

}
