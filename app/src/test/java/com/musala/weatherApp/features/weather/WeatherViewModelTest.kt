package com.musala.weatherApp.features.weather

import com.musala.weatherApp.extenions.InstantTaskExecutorExtension
import com.musala.weatherApp.utils.LocationState
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception

@ExtendWith(value = [InstantTaskExecutorExtension::class])
internal class WeatherViewModelTest{

    private lateinit var viewModel : WeatherViewModel

    @BeforeEach
    fun setup(){
        viewModel = WeatherViewModel()
    }

    @Test
    fun `when location permission not granted then state must be FetchLocationFailed`(){

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationPermissionResult(false))

        // then
        viewModel.viewState.value shouldBeEqualTo WeatherViewStates.FetchLocationFailed
    }

    @Test
    fun `when location permission granted then no any actions`(){

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationPermissionResult(true))

        // then
        viewModel.viewState.value shouldBeEqualTo WeatherViewStates.FetchingCurrentLocation
    }

    @Test
    fun `when location provider is off then state must be FetchLocationFailed`(){

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationProviderResult(false))

        // then
        viewModel.viewState.value shouldBeEqualTo WeatherViewStates.FetchLocationFailed
    }

    @Test
    fun `when location provider is on then no any actions`(){

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationProviderResult(true))

        // then
        viewModel.viewState.value shouldBeEqualTo WeatherViewStates.FetchingCurrentLocation
    }

    @Test
    fun `when failed to fetch location then state must be FetchLocationFailed`(){

        // when
        viewModel.onLocationStateUpdated(LocationState.LocationFetchFailure(Exception()))

        // then
        viewModel.viewState.value shouldBeEqualTo WeatherViewStates.FetchLocationFailed
    }


}