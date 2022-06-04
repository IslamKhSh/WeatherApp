package com.musala.weatherApp.features.weather

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.musala.weatherApp.extenions.CoroutinesTestExtension
import com.musala.weatherApp.extenions.InstantTaskExecutorExtension
import com.musala.weatherApp.features.weather.WeatherAction.OnPlaceSelected
import com.musala.weatherApp.features.weather.WeatherViewState.FetchingCurrentLocation
import com.musala.weatherApp.features.weather.WeatherViewState.FetchingPlaceWeather
import com.musala.weatherApp.utils.LocationState
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
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

    @BeforeEach
    fun setup() {
        /** mainDispatcher will be set to `TestDispatcher`, see [CoroutinesTestExtension] **/
        viewModel = WeatherViewModel(Dispatchers.Main, Dispatchers.Main, geocoder)
    }

    @Test
    fun `when location permission not granted then state must be FetchLocationFailed`() {

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationPermissionResult(false))

        // then
        viewModel.viewState.value shouldBeEqualTo WeatherViewState.FetchLocationFailed
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
        viewModel.viewState.value shouldBeEqualTo WeatherViewState.FetchLocationFailed
    }

    @Test
    fun `when location provider is on then no any actions`() {

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationProviderResult(true))

        // then
        viewModel.viewState.value shouldBeEqualTo FetchingCurrentLocation
    }

    @Test
    fun `when failed to fetch location then state must be FetchLocationFailed`() {

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationFetchFailure(Exception()))

        // then
        viewModel.viewState.value shouldBeEqualTo WeatherViewState.FetchLocationFailed
    }

    @Test
    fun `when fetch location then location must be geocoded and state must be FetchingPlaceWeather`() {
        // given
        val lat = 10.0
        val lng = 20.0
        val location = mockk<Location>()
        every { location.latitude } returns lat
        every { location.longitude } returns lng

        val cityName = "Cairo"
        val address = mockk<Address>()
        every { address.locality } returns cityName
        every { geocoder.getFromLocation(lat, lng, 1) } returns listOf(address)

        // when
        runTest {
            viewModel.onLocationStateUpdated(LocationState.LocationFetched(location))
        }

        // then
        verify { geocoder.getFromLocation(lat, lng, 1) }
        viewModel.viewState.value shouldBeEqualTo FetchingPlaceWeather(cityName, LatLng(lat, lng))
    }

    @Test
    fun `when select place then state must be FetchingPlaceWeather`() {
        // given
        val cityName = "Cairo"
        val latLng = LatLng(10.0, 20.0)
        val place = mockk<Place>()
        every { place.name } returns cityName
        every { place.latLng } returns latLng

        // when
        viewModel.onPlaceSelected(place)

        // then
        viewModel.viewState.value shouldBeEqualTo FetchingPlaceWeather(cityName, latLng)
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
                arguments(WeatherAction.OnFetchLocationError, WeatherViewState.FetchLocationFailed),
                arguments(OnPlaceSelected(cityName, latLng), FetchingPlaceWeather(cityName, latLng))
            )
        }
    }

}