package com.musala.weatherApp.data.mapper


import com.musala.weatherApp.data.entities.WeatherEntity
import com.musala.weatherApp.domain.entity.*
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class WeatherEntityMapperTest {

    @RelaxedMockK
    private lateinit var weatherEntity: WeatherEntity

    private lateinit var mapper: WeatherEntityMapper

    @BeforeEach
    fun setUp() {
        mapper = WeatherEntityMapper()
    }

    @Test
    fun `given dataModel with some params - invoke map method - then params mapped correctly`() {
        // given
        every { weatherEntity.main.temp } returns TEMPERATURE.toDouble()
        every { weatherEntity.main.tempMax } returns MAX_TEMP.toDouble()
        every { weatherEntity.main.tempMin } returns MIN_TEMP.toDouble()
        every { weatherEntity.main.feelsLike } returns TEMP_FEELS_LIKE.toDouble()
        every { weatherEntity.clouds.all } returns CLOUDS
        every { weatherEntity.wind.speed } returns WIND_M_S
        every { weatherEntity.main.humidity } returns HUMIDITY
        every { weatherEntity.weather.first().description } returns DESCRIPTION
        every { weatherEntity.weather.first().icon } returns ICON

        // when
        val result = with(mapper) { weatherEntity.map() }

        // then
        result shouldBeEqualTo currentWeatherFixture
    }

    @Test
    fun `given speed in m per s - invoke toKmPerHour - then speed must be km per h`() {

        // when
        val result = with(mapper) { WIND_M_S.toKmPerHour() }

        // then
        result shouldBeEqualTo WIND_KM_H
    }



}