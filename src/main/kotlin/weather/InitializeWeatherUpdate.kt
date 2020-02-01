package weather

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import weather.repositories.CityRepository
import weather.repositories.WeatherRepository
import weather.services.WeatherWebClientService

@Component
class InitializeWeatherUpdate : ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    lateinit var weatherWebClientService: WeatherWebClientService
    @Autowired
    lateinit var cityRepository: CityRepository
    @Autowired
    lateinit var scheduleTaskService: ScheduleTaskService
    @Autowired
    lateinit var weatherRepository: WeatherRepository

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        cityRepository
                .findAll()
                .filter { it.tracking }
                .map { city ->
                    scheduleTaskService.addTaskToScheduler(
                            city = city.name,
                            task = WeatherUpdateThread(city.name, cityRepository, weatherWebClientService, weatherRepository),
                            delay = city.interval
                    )
                }
    }

}
