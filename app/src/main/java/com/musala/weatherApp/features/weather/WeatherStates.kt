package com.musala.weatherApp.features.weather

import com.google.android.gms.maps.model.LatLng
import com.musala.weatherApp.core.base.states.BaseAction
import com.musala.weatherApp.core.base.states.BaseViewState

sealed class WeatherViewState(
    override val showLoadingIndicator: Boolean = false
) : BaseViewState {
    object FetchingCurrentLocation : WeatherViewState()
    object FetchLocationFailed : WeatherViewState()
    data class FetchingPlaceWeather(val cityName : String, val latLng: LatLng) : WeatherViewState(true)
}

sealed interface WeatherAction : BaseAction {
    object OnFetchLocationError : WeatherAction
    data class OnPlaceSelected(val cityName : String, val latLng: LatLng) :WeatherAction
}