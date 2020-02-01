package weather.services

import reactor.core.publisher.Mono
import weather.dto.bodies.ChangeTrackingBody
import weather.dto.bodies.CityNameBody
import weather.dto.bodies.ChangeIntervalBody
import weather.dto.responses.WeatherResponse

interface WeatherService {

    fun addCity(body: CityNameBody ): Mono<Boolean>
    fun removeCity(body: CityNameBody ): Boolean
    fun changeInterval(body: ChangeIntervalBody): Boolean
    fun saveCityWithWeather(cityName: String, weatherResponse: WeatherResponse): Boolean
    fun changeTracking(body: ChangeTrackingBody): Boolean

}
