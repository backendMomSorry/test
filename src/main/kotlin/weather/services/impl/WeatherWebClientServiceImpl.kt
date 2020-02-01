package weather.services.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import weather.dto.responses.WeatherResponse
import weather.services.WeatherWebClientService

const val WEATHER_URL = "http://api.openweathermap.org"
const val APPID = "2c6b8ef8022208656ba2de7950f97f93"

@Service
class WeatherWebClientServiceImpl : WeatherWebClientService, BaseService() {

    override fun getWeather(city: String): Mono<WeatherResponse> =
            buildWebClient(WEATHER_URL, "weather")
                .get()
                .uri {
                    it
                            .path("data/2.5/weather")
                            .queryParam("q", city)
                            .queryParam("APPID", APPID)
                            .build()
                }
                .retrieve()
                .bodyToMono(WeatherResponse::class.java)

}
