package weather.handlers

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import weather.dto.base.BaseException
import weather.dto.responses.ApiError
import weather.dto.base.BaseResponse

abstract class BaseHandler {

    private var logger = LoggerFactory.getLogger(BaseHandler::class.java)

    inline fun <reified TBody, reified TResponse> handleMono(
            request: ServerRequest,
            crossinline responseFunc: (TBody) -> Mono<TResponse>
    ): Mono<ServerResponse> =
            request
                    .bodyToMono(TBody::class.java)
                    .flatMap {
                        responseFunc(it)
                    }
                    .flatMap {
                        createOkResponse(it)
                    }
                    .onErrorResume {
                        onErrorResume(it)
                    }

    inline fun <reified TBody, reified TResponse> handle(
            request: ServerRequest,
            crossinline responseFunc: (TBody) -> TResponse
    ): Mono<ServerResponse> =
            request
                    .bodyToMono(TBody::class.java)
                    .map {
                        responseFunc(it)
                    }
                    .flatMap {
                        createOkResponse(it)
                    }
                    .onErrorResume {
                        onErrorResume(it)
                    }

    fun onErrorResume(throwable: Throwable): Mono<ServerResponse> {
        logger.error("ERROR", throwable)
        throwable.printStackTrace()

        return when (throwable) {
            is BaseException -> {
                if (throwable.apiError.code == ApiError.INTERNAL_SERVER_ERROR.code) {
                    getInternalErrorResponse()
                } else {
                    // log
                    returnResponseWithApiError(throwable.apiError, throwable.description)
                }

            }
            else -> getInternalErrorResponse()
        }
    }

    private fun returnResponseWithApiError(apiError: ApiError, description: String? = null): Mono<ServerResponse> = ServerResponse
            .ok()
            .body(BodyInserters.fromValue(BaseResponse<BaseException>(BaseException(apiError, description))))

    fun <T> createOkResponse(response: T): Mono<ServerResponse> = ServerResponse
            .ok()
            .body(BodyInserters.fromValue(BaseResponse(response)))

    private fun getInternalErrorResponse(): Mono<ServerResponse> = ServerResponse
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(BodyInserters.fromValue(BaseResponse<BaseException>(BaseException(ApiError.INTERNAL_SERVER_ERROR))))


}
