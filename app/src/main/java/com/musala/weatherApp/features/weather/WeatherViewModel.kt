package com.musala.weatherApp.features.weather

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.musala.weatherApp.core.base.BaseViewModel
import com.musala.weatherApp.core.di.IoDispatcher
import com.musala.weatherApp.core.di.MainDispatcher
import com.musala.weatherApp.domain.entity.ErrorType
import com.musala.weatherApp.domain.entity.data
import com.musala.weatherApp.domain.entity.error
import com.musala.weatherApp.domain.entity.isSucceeded
import com.musala.weatherApp.domain.usecase.*
import com.musala.weatherApp.features.weather.WeatherAction.*
import com.musala.weatherApp.features.weather.WeatherViewState.*
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
    private val geocoder: Geocoder,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) : BaseViewModel<WeatherViewState, WeatherAction>(FetchingCurrentLocation) {

    override fun actionMapper(viewAction: WeatherAction): WeatherViewState = when (viewAction) {
        OnFetchLocationError -> WaitingSearchInput
        is OnPlaceSelected -> FetchingPlaceWeather(viewAction.cityName, viewAction.latLng)
        OnClearSelectedPlace -> WaitingSearchInput
        is OnApiError -> FetchingCurrentWeatherError(viewAction.cityName, viewAction.latLng)
        is OnConnectionError -> InternetConnectionError(viewAction.cityName, viewAction.latLng)
        is OnFetchWeatherSuccessfully ->
            DisplayCurrentWeather(viewAction.cityName, viewAction.currentWeather)
    }

    fun onLocationStateUpdated(locationState: LocationState) {
        if (locationState.isFetchingLocationFailed())
            sendAction(OnFetchLocationError)
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
                getCurrentWeather(cityName, LatLng(location.latitude, location.longitude))
            }
        }
    }


    fun onPlaceSelected(place: Place) {
        getCurrentWeather(place.name ?: return, place.latLng ?: return)
    }

    fun clearSelectedPlace() {
        sendAction(OnClearSelectedPlace)
    }

    fun getCurrentWeather(cityName: String, latLng: LatLng) {
        sendAction(OnPlaceSelected(cityName, latLng))
        viewModelScope.launch {
            with(getCurrentWeatherUseCase(latLng.latitude to latLng.longitude)) {
                if (isSucceeded)
                    sendAction(OnFetchWeatherSuccessfully(cityName, data))
                else if (error?.errorType is ErrorType.InternetConnection || error?.errorType is ErrorType.TimeOut)
                    sendAction(OnConnectionError(cityName, latLng))
                else
                    sendAction(OnApiError(cityName, latLng))
            }
        }
    }
}