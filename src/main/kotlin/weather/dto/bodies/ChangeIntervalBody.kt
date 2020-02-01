package weather.dto.bodies

class ChangeIntervalBody(val interval: Long, override val city: String) : CityNameBodyInterface
