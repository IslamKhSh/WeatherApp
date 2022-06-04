package com.musala.weatherApp.features.weather

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.musala.weatherApp.core.base.BaseViewModel
import com.musala.weatherApp.core.di.IoDispatcher
import com.musala.weatherApp.core.di.MainDispatcher
import com.musala.weatherApp.features.weather.WeatherAction.OnPlaceSelected
import com.musala.weatherApp.features.weather.WeatherViewState.FetchingCurrentLocation
import com.musala.weatherApp.features.weather.WeatherViewState.FetchingPlaceWeather
import com.musala.weatherApp.utils.LocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val geocoder: Geocoder
) : BaseViewModel<WeatherViewState, WeatherAction>(FetchingCurrentLocation) {

    override fun actionMapper(viewAction: WeatherAction): WeatherViewState = when (viewAction) {
        WeatherAction.OnFetchLocationError -> WeatherViewState.FetchLocationFailed
        is OnPlaceSelected -> FetchingPlaceWeather(viewAction.cityName, viewAction.latLng)
    }

    fun onLocationStateUpdated(locationState: LocationState) {
        if (locationState.isFetchingLocationFailed())
            sendAction(WeatherAction.OnFetchLocationError)
        else if (locationState is LocationState.LocationFetched)
            viewModelScope.launch {
                onUserLocationFetched(locationState.location)
            }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun onUserLocationFetched(location: Location) {
        withContext(ioDispatcher) {
            val cityName = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .firstOrNull()?.let { it.locality ?: it.subAdminArea } ?: return@withContext

            withContext(mainDispatcher) {
                sendAction(OnPlaceSelected(cityName, LatLng(location.latitude, location.longitude)))
            }
        }
    }


    fun onPlaceSelected(place: Place) {
        sendAction(OnPlaceSelected(place.name ?: return, place.latLng ?: return))
    }
}