package weather.dto.base

import weather.dto.enums.ApiError

class BaseException(
        val apiError: ApiError,
        val description: String?
) : RuntimeException(apiError.description) {

    constructor(apiError: ApiError) : this(apiError, null)

}
