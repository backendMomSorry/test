package weather

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import weather.handlers.WeatherHandler

@Configuration
class RoutingConfig {

    @Autowired
    lateinit var weatherHandler: WeatherHandler

    @Bean
    fun routes(): RouterFunction<ServerResponse> = router {
        "/weather/city".nest {
            POST("/add")(weatherHandler::add)
            POST("/remove")(weatherHandler::remove)
            POST("/change/interval")(weatherHandler::changeInterval)
            POST("/change/tracking")(weatherHandler::changeTracking)
        }
    }.filter { request, next ->
        //todo add log
        next.handle(request)
    }

}
