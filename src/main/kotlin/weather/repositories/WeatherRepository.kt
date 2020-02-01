package weather.repositories

import org.springframework.data.repository.CrudRepository
import weather.models.Weather

interface WeatherRepository : CrudRepository<Weather, Long> {
}
