package weather.services.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import weather.dto.base.BaseException
import weather.dto.enums.ApiError
import weather.dto.enums.WeatherError
import weather.dto.responses.WeatherErrorResponse
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
                    .exchange()
                    .flatMap { response ->
                        when (response.statusCode()) {
                            HttpStatus.OK -> response.bodyToMono(WeatherResponse::class.java)
                            else -> response
                                    .bodyToMono(WeatherErrorResponse::class.java)
                                    .map {
                                        when (it.message) {
                                            WeatherError.CITY_NOT_FOUND.message -> throw BaseException(ApiError.CITY_NOT_FOUND)
                                            else -> throw BaseException(ApiError.INTERNAL_SERVER_ERROR)
                                        }
                                    }
                        }
                    }

}
