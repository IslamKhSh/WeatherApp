package com.musala.weatherApp.domain.usecase

import com.musala.weatherApp.domain.entity.ErrorType
import com.musala.weatherApp.domain.entity.Result
import com.musala.weatherApp.domain.entity.currentWeatherFixture
import com.musala.weatherApp.domain.entity.data
import com.musala.weatherApp.domain.error.ApiErrorManager
import com.musala.weatherApp.domain.repository.WeatherRepo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.UnknownHostException

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
internal class GetCurrentWeatherUseCaseTest {

    @MockK
    private lateinit var weatherRepo: WeatherRepo

    @MockK
    private lateinit var errorManager: ApiErrorManager

    private lateinit var useCase: GetCurrentWeatherUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetCurrentWeatherUseCase(errorManager, weatherRepo)
    }

    @Test
    fun `given repo - when execute useCase - then verify repo function called`() {
        // given
        coEvery { weatherRepo.getCurrentWeather(any(), any()) } returns currentWeatherFixture
        val lat = 10.0
        val lng = 20.0

        // when
        runTest { useCase(lat to lng) }

        // then
        coVerify(exactly = 1) { weatherRepo.getCurrentWeather(lat, lng) }
    }

    @Test
    fun `given repo getProductDetails successfully - when execute useCase - then result should be success`() {
        // given
        coEvery { weatherRepo.getCurrentWeather(any(), any()) } returns currentWeatherFixture

        // when
        val result = runBlocking { useCase(10.0 to 20.0) }

        // then
        result shouldBeEqualTo Result.Success(currentWeatherFixture)
        result.data.shouldNotBeNull()
        result.data shouldBeEqualTo currentWeatherFixture
    }

    @Test
    fun `given repo throw exception - when execute useCase - result should be error`() {
        // given
        val exception = UnknownHostException()
        coEvery { weatherRepo.getCurrentWeather(any(), any()) } throws exception
        every { errorManager.catch(exception) } returns Result.Error(ErrorType.Other)

        // when
        val result = runBlocking { useCase(10.0 to 20.0) }

        // then
        result shouldBeEqualTo Result.Error(ErrorType.Other)
    }
}