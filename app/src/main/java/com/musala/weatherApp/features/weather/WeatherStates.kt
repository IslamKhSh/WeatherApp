package com.musala.weatherApp.features.weather

import com.musala.weatherApp.core.base.states.BaseAction
import com.musala.weatherApp.core.base.states.BaseViewState

sealed class WeatherViewStates(
    override val showLoadingIndicator: Boolean = false
) : BaseViewState {
    object FetchingCurrentLocation : WeatherViewStates()
    object FetchLocationFailed : WeatherViewStates()

}

sealed interface WeatherAction : BaseAction {
    object OnFetchLocationError : WeatherAction

}