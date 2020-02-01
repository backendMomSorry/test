package weather.dto.base

import weather.dto.enums.ApiError

class BaseResponse<T>(
        val result: T?,
        val errorCode: Int,
        val errorMessage: String?
) {

    constructor(result: T) : this(result, 0, null)

    constructor(exception: Exception) : this(null, (exception as? BaseException)?.apiError?.code
            ?: ApiError.INTERNAL_SERVER_ERROR.code, exception.message)

}
