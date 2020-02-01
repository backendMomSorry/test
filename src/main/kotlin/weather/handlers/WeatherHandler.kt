package weather.handlers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import weather.dto.bodies.CityNameBody
import weather.dto.bodies.ChangeIntervalBody
import weather.dto.bodies.ChangeTrackingBody
import weather.services.WeatherService

@Component
@Suppress("detekt.TooGenericExceptionThrown")
class WeatherHandler(@Autowired val weatherService: WeatherService) : BaseHandler() {

    fun add(request: ServerRequest): Mono<ServerResponse> = handleMono<CityNameBody, Boolean>(request) { body ->
        weatherService.addCity(body)
    }

    fun remove(request: ServerRequest): Mono<ServerResponse> = handle<CityNameBody, Boolean>(request) { body ->
        weatherService.removeCity(body)
    }

    fun changeInterval(request: ServerRequest): Mono<ServerResponse> = handle<ChangeIntervalBody, Boolean>(request) { body ->
        weatherService.changeInterval(body)
    }

    fun changeTracking(request: ServerRequest): Mono<ServerResponse> = handle<ChangeTrackingBody, Boolean>(request) { body ->
        weatherService.changeTracking(body)
    }

}
