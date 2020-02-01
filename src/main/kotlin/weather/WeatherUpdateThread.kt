package weather

import weather.models.Weather
import weather.repositories.CityRepository
import weather.repositories.WeatherRepository
import weather.services.WeatherWebClientService
import java.time.ZonedDateTime

class WeatherUpdateThread(
        private val cityName: String,
        private val cityRepository: CityRepository,
        private val weatherWebClientService: WeatherWebClientService,
        private val weatherRepository: WeatherRepository
) : Thread() {

    override fun run() {
        val city = cityRepository.findByName(cityName)!!

        weatherWebClientService
                .getWeather(cityName)
                .map {
                    val weather = Weather().apply {
                        this.id = city.id
                        main = it.weather.first().main
                        description = it.weather.first().description
                    }

                    weatherRepository.save(weather)
                }
                .block()

        println("weather in city $cityName updated ${ZonedDateTime.now()}");
    }

}
