package weather.services.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import weather.ScheduleTaskService
import weather.WeatherUpdateThread
import weather.dto.base.BaseException
import weather.dto.bodies.ChangeIntervalBody
import weather.dto.bodies.ChangeTrackingBody
import weather.dto.bodies.CityNameBody
import weather.dto.responses.ApiError
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
    lateinit var scheduleTaskService: ScheduleTaskService
    @Autowired
    lateinit var weatherRepository: WeatherRepository

    override fun addCity(body: CityNameBody): Mono<Boolean> {
        cityRepository
                .findByName(body.city)
                ?.let {
                    throw BaseException(ApiError.CITY_ALREADY_ADDED)
                }

        return weatherWebClientService
                .getWeather(body.city)
                .map {
                    weatherService.saveCityWithWeather(body.city, it)

                    scheduleTaskService.addTaskToScheduler(
                            city = body.city,
                            task = WeatherUpdateThread(body.city, cityRepository, weatherWebClientService, weatherRepository),
                            delay = DEFAULT_INTERVAL
                    )

                    true
                }
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
        scheduleTaskService.removeTaskFromScheduler(body.city)

        return cityRepository.deleteByName(body.city) != 0
    }

    @Transactional
    override fun changeInterval(body: ChangeIntervalBody): Boolean {
        val city = cityRepository.findByName(body.city) ?: throw BaseException(ApiError.CITY_NOT_FOUND)
        city.interval = body.interval
        cityRepository.save(city)

        scheduleTaskService.jobsMap[body.city]?.cancel(false)

        scheduleTaskService.addTaskToScheduler(
                city = body.city,
                task = WeatherUpdateThread(body.city, cityRepository, weatherWebClientService, weatherRepository),
                delay = body.interval
        )

        return true
    }

    @Transactional
    override fun changeTracking(body: ChangeTrackingBody): Boolean {
        val city = cityRepository.findByName(body.city) ?: throw BaseException(ApiError.CITY_NOT_FOUND)
        city.tracking = body.tracking
        cityRepository.save(city)

        when (body.tracking) {
            true -> scheduleTaskService.addTaskToScheduler(
                    city = city.name,
                    task = WeatherUpdateThread(body.city, cityRepository, weatherWebClientService, weatherRepository),
                    delay = city.interval
            )
            else -> scheduleTaskService.removeTaskFromScheduler(city.name)
        }

        return true
    }

}
