package weather.services

import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import weather.dto.Weather
import weather.dto.base.BaseException
import weather.dto.bodies.ChangeIntervalBody
import weather.dto.bodies.ChangeTrackingBody
import weather.dto.enums.ApiError
import weather.dto.responses.WeatherResponse
import weather.repositories.CityRepository
import weather.repositories.MockRepository
import weather.repositories.WeatherRepository
import weather.services.impl.WeatherServiceImpl

class WeatherServiceTest {

    private val cityRepository = MockRepository(mock<CityRepository>()) { it.id }.mock
    private val weatherRepository = MockRepository(mock<WeatherRepository>()) { it.id }.mock
    private inline fun <reified T> mock(clazz: Class<T> = T::class.java): T = Mockito.mock(clazz)

    private val weatherService = WeatherServiceImpl().also {
        it.cityRepository = cityRepository
        it.weatherRepository = weatherRepository
    }

    @Test
    fun changeIntervalForIncorrectCity() {
        try {
            weatherService.changeInterval(ChangeIntervalBody(10000, "sdfsdfsdf"))
        } catch (e: BaseException) {
            Assert.assertEquals(ApiError.CITY_NOT_FOUND, e.apiError)
        }
    }

    @Test
    fun changeTrackingForIncorrectCity() {
        try {
            weatherService.changeTracking(ChangeTrackingBody("sdfsf", false))
        } catch (e: BaseException) {
            Assert.assertEquals(ApiError.CITY_NOT_FOUND, e.apiError)
        }
    }

    @Test
    fun saveWeather() {
        val result = weatherService.saveCityWithWeather("london", WeatherResponse(listOf(Weather(1, "123", "sdsd"))))
        Assert.assertEquals(result, true)
    }

}
