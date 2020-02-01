package weather.dto.responses

enum class ApiError(val code: Int, val description: String) {

    CITY_ALREADY_ADDED(1, "Город уже добавлен"),
    CITY_NOT_FOUND(2, "Город не найден"),
    INTERNAL_SERVER_ERROR(500, "Внутренняя ошибка сервера.")

}
