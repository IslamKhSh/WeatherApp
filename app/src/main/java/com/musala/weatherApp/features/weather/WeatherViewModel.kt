package com.musala.weatherApp.features.weather

import com.musala.weatherApp.core.base.BaseViewModel
import com.musala.weatherApp.utils.LocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor() : BaseViewModel<WeatherViewStates, WeatherAction>(
    WeatherViewStates.FetchingCurrentLocation
) {

    override fun actionMapper(viewAction: WeatherAction): WeatherViewStates = when (viewAction) {
        WeatherAction.OnFetchLocationError -> WeatherViewStates.FetchLocationFailed
    }

    fun onLocationStateUpdated(locationState: LocationState) {
        if (locationState.isFetchingLocationFailed()) {
            sendAction(WeatherAction.OnFetchLocationError)
        } else if (locationState is LocationState.LocationFetched) {

        }
    }
}