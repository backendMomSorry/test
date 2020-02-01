package weather.dto.bodies

interface CityNameBodyInterface {

    val city: String
    fun getCityName(): String = city.toLowerCase()

}
