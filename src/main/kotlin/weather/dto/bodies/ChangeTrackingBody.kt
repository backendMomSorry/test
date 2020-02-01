package weather.dto.bodies

class ChangeTrackingBody(override val city: String, val tracking: Boolean): CityNameBodyInterface
