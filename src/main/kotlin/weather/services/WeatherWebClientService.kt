package weather.services

import reactor.core.publisher.Mono
import weather.dto.responses.WeatherResponse

interface WeatherWebClientService {

    fun getWeather(city: String): Mono<WeatherResponse>

}
