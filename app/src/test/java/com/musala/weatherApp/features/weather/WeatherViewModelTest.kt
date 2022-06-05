package com.musala.weatherApp.features.weather

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.musala.weatherApp.domain.entity.ErrorType
import com.musala.weatherApp.domain.entity.Result
import com.musala.weatherApp.domain.entity.currentWeatherFixture
import com.musala.weatherApp.domain.usecase.GetCurrentWeatherUseCase
import com.musala.weatherApp.extenions.CoroutinesTestExtension
import com.musala.weatherApp.extenions.InstantTaskExecutorExtension
import com.musala.weatherApp.features.weather.WeatherAction.*
import com.musala.weatherApp.features.weather.WeatherViewState.*
import com.musala.weatherApp.testUtils.createMockedObserver
import com.musala.weatherApp.testUtils.getLiveDataChanges
import com.musala.weatherApp.utils.LocationState
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalCoroutinesApi
@ExtendWith(value = [InstantTaskExecutorExtension::class, MockKExtension::class, CoroutinesTestExtension::class])
internal class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel

    @MockK
    private lateinit var geocoder: Geocoder

    @MockK
    private lateinit var useCase: GetCurrentWeatherUseCase

    private val stateObserver = createMockedObserver<WeatherViewState>()

    private val cityName = "Cairo"
    private val latLng = LatLng(10.0, 20.0)

    @BeforeEach
    fun setup() {
        /** mainDispatcher will be set to `TestDispatcher`, see [CoroutinesTestExtension] **/
        viewModel = WeatherViewModel(Dispatchers.Main, Dispatchers.Main, geocoder, useCase)
        viewModel.viewState.observeForever(stateObserver)
    }

    @Test
    fun `when location permission not granted then state must be FetchLocationFailed`() {

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationPermissionResult(false))

        // then
        viewModel.viewState.value shouldBeEqualTo WaitingSearchInput
    }

    @Test
    fun `when location permission granted then no any actions`() {

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationPermissionResult(true))

        // then
        viewModel.viewState.value shouldBeEqualTo FetchingCurrentLocation
    }

    @Test
    fun `when location provider is off then state must be FetchLocationFailed`() {

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationProviderResult(false))

        // then
        viewModel.viewState.value shouldBeEqualTo WaitingSearchInput
    }

    @Test
    fun `when location provider is on then no any actions`() {

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationProviderResult(true))

        // then
        viewModel.viewState.value shouldBeEqualTo FetchingCurrentLocation
    }

    @Test
    fun `when failed to fetch location then state must be WaitingSearchInput`() {

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationFetchFailure(Exception()))

        // then
        viewModel.viewState.value shouldBeEqualTo WaitingSearchInput
    }

    @Test
    fun `when fetch location, location must be geocoded, weather must be fetched and state must be changed`() {
        // given
        val location = mockk<Location>()
        every { location.latitude } returns latLng.latitude
        every { location.longitude } returns latLng.longitude

        val address = mockk<Address>()
        every { address.locality } returns cityName
        every { geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) } returns listOf(address)

        coEvery { useCase(any()) } returns Result.Success(currentWeatherFixture)

        // when
        runTest {
            viewModel.onLocationStateUpdated(LocationState.LocationFetched(location))
        }

        // then
        verify { geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) }
        coVerify { useCase(any()) }
        getLiveDataChanges(stateObserver) shouldBeEqualTo listOf(
            FetchingCurrentLocation,
            FetchingPlaceWeather(cityName, latLng),
            DisplayCurrentWeather(cityName, currentWeatherFixture)
        )
    }

    @Test
    fun `when select place then weather must be fetched and state must be changed`() {
        // given
        val place = mockk<Place>()
        every { place.name } returns cityName
        every { place.latLng } returns latLng

        coEvery { useCase(any()) } returns Result.Success(currentWeatherFixture)

        // when
        runTest {
            viewModel.onPlaceSelected(place)
        }

        // then
        coVerify { useCase(any()) }
        getLiveDataChanges(stateObserver) shouldBeEqualTo listOf(
            FetchingCurrentLocation,
            FetchingPlaceWeather(cityName, latLng),
            DisplayCurrentWeather(cityName, currentWeatherFixture)
        )
    }

    @Test
    fun `when clear selected place then state must be WaitingSearchInput`() {

        // when
        viewModel.clearSelectedPlace()

        // then
        viewModel.viewState.value shouldBeEqualTo WaitingSearchInput
    }

    @Test
    fun `when getCurrentWeather success then state must be changed`() {
        // given
        coEvery { useCase(any()) } returns Result.Success(currentWeatherFixture)

        // when
        runTest {
            viewModel.getCurrentWeather(cityName, latLng)
        }

        // then
        getLiveDataChanges(stateObserver) shouldBeEqualTo listOf(
            FetchingCurrentLocation,
            FetchingPlaceWeather(cityName, latLng),
            DisplayCurrentWeather(cityName, currentWeatherFixture)
        )
    }

    @Test
    fun `when getCurrentWeather with network error then state must be changed`() {
        // given
        coEvery { useCase(any()) } returns Result.Error(ErrorType.InternetConnection)

        // when
        runTest {
            viewModel.getCurrentWeather(cityName, latLng)
        }

        // then
        getLiveDataChanges(stateObserver) shouldBeEqualTo listOf(
            FetchingCurrentLocation,
            FetchingPlaceWeather(cityName, latLng),
            InternetConnectionError(cityName, latLng)
        )
    }

    @Test
    fun `when getCurrentWeather with api error then state must be changed`() {
        // given
        coEvery { useCase(any()) } returns Result.Error(ErrorType.Other)

        // when
        runTest {
            viewModel.getCurrentWeather(cityName, latLng)
        }

        // then
        getLiveDataChanges(stateObserver) shouldBeEqualTo listOf(
            FetchingCurrentLocation,
            FetchingPlaceWeather(cityName, latLng),
            FetchingCurrentWeatherError(cityName, latLng)
        )
    }

    @ParameterizedTest
    @MethodSource("weatherActionsToStates")
    fun `given list of actions - when invoke send action - then it mapped to correct state`(
        action: WeatherAction,
        viewState: WeatherViewState
    ) {

        // when
        viewModel.sendAction(action)

        // then
        viewModel.viewState.value shouldBeEqualTo viewState
    }

    companion object {
        @JvmStatic
        @Suppress("UnusedPrivateMember")
        private fun weatherActionsToStates(): Stream<Arguments?>? {
            val cityName = "Cairo"
            val latLng = LatLng(10.0, 20.0)

            return Stream.of(
                arguments(OnFetchLocationError, WaitingSearchInput),
                arguments(
                    OnPlaceSelected(cityName, latLng), FetchingPlaceWeather(cityName, latLng)
                ),
                arguments(OnClearSelectedPlace, WaitingSearchInput),
                arguments(
                    OnApiError(cityName, latLng), FetchingCurrentWeatherError(cityName, latLng)
                ),
                arguments(
                    OnConnectionError(cityName, latLng), InternetConnectionError(cityName, latLng)
                ),
                arguments(
                    OnFetchWeatherSuccessfully(cityName, currentWeatherFixture),
                    DisplayCurrentWeather(cityName, currentWeatherFixture)
                )
            )
        }
    }

}