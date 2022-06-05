package com.musala.weatherApp.features.weather

import com.google.android.gms.maps.model.LatLng
import com.musala.weatherApp.core.base.states.BaseAction
import com.musala.weatherApp.core.base.states.BaseViewState
import com.musala.weatherApp.domain.entity.CurrentWeather


sealed interface WeatherViewState : BaseViewState {
    object FetchingCurrentLocation : WeatherViewState
    object WaitingSearchInput : WeatherViewState
    data class FetchingPlaceWeather(val cityName: String, val latLng: LatLng) : WeatherViewState
    data class DisplayCurrentWeather(val cityName: String, val currentWeather: CurrentWeather) : WeatherViewState
    data class InternetConnectionError(val cityName: String, val latLng: LatLng) : WeatherViewState
    data class FetchingCurrentWeatherError(val cityName: String, val latLng: LatLng) : WeatherViewState
}

sealed interface WeatherAction : BaseAction {
    object OnFetchLocationError : WeatherAction
    data class OnPlaceSelected(val cityName: String, val latLng: LatLng) : WeatherAction
    data class OnFetchWeatherSuccessfully(val cityName: String, val currentWeather: CurrentWeather) : WeatherAction
    data class OnConnectionError(val cityName: String, val latLng: LatLng) : WeatherAction
    data class OnApiError(val cityName: String, val latLng: LatLng) : WeatherAction
    object OnClearSelectedPlace : WeatherAction
}