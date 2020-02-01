package weather.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import weather.ScheduleWeatherUpdateService
import weather.WeatherUpdateThread
import weather.dto.base.BaseException
import weather.dto.bodies.ChangeIntervalBody
import weather.dto.bodies.ChangeTrackingBody
import weather.dto.bodies.CityNameBody
import weather.dto.enums.ApiError
import weather.dto.responses.WeatherResponse
import weather.models.City
import weather.models.DEFAULT_INTERVAL
import weather.models.Weather
import weather.repositories.CityRepository
import weather.repositories.WeatherRepository
import weather.services.WeatherService
import weather.services.WeatherWebClientService
import javax.transaction.Transactional

@Service
class WeatherServiceImpl : WeatherService {

    @Autowired
    lateinit var weatherWebClientService: WeatherWebClientService
    @Autowired
    lateinit var cityRepository: CityRepository
    @Autowired
    lateinit var weatherService: WeatherService
    @Autowired
    lateinit var scheduleWeatherUpdateService: ScheduleWeatherUpdateService
    @Autowired
    lateinit var weatherRepository: WeatherRepository

    override fun addCity(body: CityNameBody): Mono<Boolean> {
        cityRepository
                .findByName(body.getCityName())
                ?.let {
                    throw BaseException(ApiError.CITY_ALREADY_ADDED)
                }

        return weatherWebClientService
                .getWeather(body.getCityName())
                .map {
                    weatherService.saveCityWithWeather(body.getCityName(), it)

                    addTaskToScheduler(body.getCityName())

                    true
                }
    }

    private fun addTaskToScheduler(city: String, interval: Long = DEFAULT_INTERVAL) {
        scheduleWeatherUpdateService.addTaskToScheduler(
                city = city,
                task = WeatherUpdateThread(city, cityRepository, weatherWebClientService, weatherRepository),
                delay = interval
        )
    }

    @Transactional
    override fun saveCityWithWeather(cityName: String, weatherResponse: WeatherResponse): Boolean {
        val city = City().apply {
            name = cityName
        }
        cityRepository.save(city)     // add check unique

        val weather = Weather().apply {
            this.id = city.id
            main = weatherResponse.weather.first().main
            description = weatherResponse.weather.first().description
        }
        weatherRepository.save(weather)

        return true
    }

    @Transactional
    override fun removeCity(body: CityNameBody): Boolean {
        scheduleWeatherUpdateService.removeTaskFromScheduler(body.getCityName())

        return cityRepository.deleteByName(body.getCityName()) != 0
    }

    @Transactional
    override fun changeInterval(body: ChangeIntervalBody): Boolean {
        val city = cityRepository.findByName(body.getCityName()) ?: throw BaseException(ApiError.CITY_NOT_FOUND)
        city.interval = body.interval
        cityRepository.save(city)

        scheduleWeatherUpdateService.jobsMap[body.getCityName()]?.cancel(false)

        addTaskToScheduler(body.getCityName(), body.interval)

        return true
    }

    @Transactional
    override fun changeTracking(body: ChangeTrackingBody): Boolean {
        val city = cityRepository.findByName(body.getCityName()) ?: throw BaseException(ApiError.CITY_NOT_FOUND)
        city.tracking = body.tracking
        cityRepository.save(city)

        when (body.tracking) {
            true -> addTaskToScheduler(city.name, city.interval)
            else -> scheduleWeatherUpdateService.removeTaskFromScheduler(city.name)
        }

        return true
    }

}
