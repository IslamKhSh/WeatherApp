package com.musala.weatherApp.data.repository

import com.musala.weatherApp.data.entities.WeatherEntity
import com.musala.weatherApp.data.mapper.WeatherEntityMapper
import com.musala.weatherApp.data.remote.ApiService
import com.musala.weatherApp.domain.entity.currentWeatherFixture
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.coInvoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
internal class WeatherRepoImpTest {

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var mapper: WeatherEntityMapper

    private lateinit var repo: WeatherRepoImp

    @BeforeEach
    fun setUp() {
        repo = WeatherRepoImp(apiService, mapper)
    }

    @Test
    fun `given success response from api - when getCurrentWeather - then result must be mapped`() {
        // given
        val response = mockk<WeatherEntity>()

        coEvery { apiService.getCurrentWeather(any(), any()) } returns response
        every { with(mapper) { response.map() } } returns currentWeatherFixture

        // when
        val result = runBlocking { repo.getCurrentWeather(10.0, 20.0) }

        // then
        verify { with(mapper) { response.map() } }
        result shouldBeEqualTo currentWeatherFixture
    }

    @Test
    fun `given api error - when getCurrentWeather - then throw same error`() {
        // given
        val error = UnknownError()
        coEvery { apiService.getCurrentWeather(any(), any()) } throws error

        // when
        runTest {
            // then
            coInvoking { repo.getCurrentWeather(10.0, 20.0) } shouldThrow error
        }
    }
}