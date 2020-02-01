package weather.repositories

import org.springframework.data.repository.CrudRepository
import weather.models.City

interface CityRepository : CrudRepository<City, Long> {

    fun findByName(name: String): City?
    fun deleteByName(name: String): Int

}
