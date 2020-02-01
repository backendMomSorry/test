package weather.services.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient

abstract class BaseService {

    val logger: Logger = LoggerFactory.getLogger(BaseService::class.java)!!

    fun buildWebClient(url: String, serviceName: String): WebClient =
            WebClient.builder()
                    .baseUrl(url)
                    .filter(logRequest(serviceName))
                    .build()

    private fun logRequest(serviceName: String) = ExchangeFilterFunction { req, res ->
        logger.info("service: $serviceName request:${req.url()}")

        res.exchange(req)
                .doOnSuccess { response ->
                    val code = response?.statusCode()?.value()
                    logger.info("service: $serviceName Success responses: ${req.url()} code: $code")
                }
                .doOnError {
                    logger.info("service: $serviceName Error responses: ${req.url()} trace:\n${it.stackTrace}")
                }
    }

}

