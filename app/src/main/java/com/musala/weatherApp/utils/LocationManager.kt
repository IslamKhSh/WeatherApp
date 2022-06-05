@file:Suppress("Detekt:WildcardImport", "Detekt:MagicNumber")

package com.musala.weatherApp.utils

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Looper
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.distinctUntilChanged
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.musala.weatherApp.core.extensions.isAllTrue
import com.musala.weatherApp.utils.LocationState.LocationPermissionResult
import java.lang.ref.WeakReference

sealed class LocationState {
    data class LocationPermissionResult(val isGranted: Boolean) : LocationState()
    data class LocationProviderResult(val isOn: Boolean) : LocationState()
    data class LocationFetched(val location: Location) : LocationState()
    data class LocationFetchFailure(val exception: Exception) : LocationState()

    fun isFetchingLocationFailed() =
        ((this is LocationPermissionResult) && !isGranted) || // if location permission not granted
            ((this is LocationProviderResult) && !isOn) || // if GPS is off
            (this is LocationFetchFailure) // if can't fetch current location
}

class LocationManager(context: Context, resultCaller: ActivityResultCaller) {

    private val contextRef = WeakReference(context)
    private val resultCallerRef = WeakReference(resultCaller)
    private val locationStateLiveData = MutableLiveData<LocationState>()
    private var locationPermissionRequest: ActivityResultLauncher<Array<String>>? = null
    private var locationSettingsRequest: ActivityResultLauncher<IntentSenderRequest>? = null
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 1000
            numUpdates = 1
            smallestDisplacement = 170f // 170 m = 0.1 mile
        }
    }

    init {
        initLocationPermission()
        initLocationSettings()
    }

    /**
     * Call this method to get the current location and it handles all the process:
     * - Check for location permission and request it if needed.
     * - When permission granted it checks on GPS state and ask user to turn it on if needed,
     *   see [locationPermissionRequest] initialization.
     * - When GPS is on start fetching current location. see [locationSettingsRequest] initialization.
     *
     * @return LiveData of [LocationState] which will be updated with the result of every step.
     *
     * if any failure happens at any step [locationStateLiveData] will be updated
     * and the reset of steps will be canceled.
     */
    fun getCurrentLocation(): LiveData<LocationState> {
        locationPermissionRequest?.launch(arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION))
        return locationStateLiveData
    }

    fun getLocationState(): LiveData<LocationState> {
        if (locationStateLiveData.value == null) getCurrentLocation()
        return locationStateLiveData
    }

    fun isLocationEnabled(): Boolean {
        val locationManager =
            contextRef.get()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isNetWorkProviderEnabled = locationManager.isProviderEnabled(NETWORK_PROVIDER)
        val isGpsProviderEnabled = locationManager.isProviderEnabled(GPS_PROVIDER)
        return (isNetWorkProviderEnabled || isGpsProviderEnabled)
    }

    fun isLocationPermissionGrantedLiveData(): LiveData<Boolean> {
        return Transformations.map(getLocationState()) {
            it is LocationPermissionResult && it.isGranted
        }.distinctUntilChanged()
    }

    /**
     * init the result of location permission check and if granted check location settings.
     */
    private fun initLocationPermission() {
        locationPermissionRequest =
            resultCallerRef.get()?.registerForActivityResult(RequestMultiplePermissions()) {
                if (it.values.isAllTrue()) { // permission granted
                    checkLocationSettings()
                    locationStateLiveData.value = LocationPermissionResult(true)
                } else { // permission denied
                    locationStateLiveData.value = LocationPermissionResult(false)
                }
            }
    }

    /**
     * init the result of ask user to enable location and if user accept start detect current location.
     */
    private fun initLocationSettings() {
        locationSettingsRequest =
            resultCallerRef.get()?.registerForActivityResult(StartIntentSenderForResult()) {
                if (it.resultCode == Activity.RESULT_OK) { // user accept to enable location
                    locationStateLiveData.value = LocationState.LocationProviderResult(true)
                    fetchLocation()
                } else { // user reject
                    locationStateLiveData.value = LocationState.LocationProviderResult(false)
                }
            }
    }

    /**
     * check location settings (if location is on or off)
     *
     * if location is on start detect current location else ask user to enable it.
     */
    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
        // check if location provider is on or off
        SettingsClient(contextRef.get() ?: return)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener { fetchLocation() } // location is on
            .addOnFailureListener {
                // location is off, ask user to enable it
                if (it is ResolvableApiException) {
                    locationSettingsRequest?.launch(
                        IntentSenderRequest.Builder(it.resolution).build()
                    )
                }
            }
    }

    /**
     * listen for location change and get it when it's ready
     */
    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(contextRef.get() ?: return)
        // location change listener
        val listener = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationProviderClient.removeLocationUpdates(this)
                locationResult.lastLocation.let {
                    locationStateLiveData.value = LocationState.LocationFetched(it)
                }
            }
        }
        // start listen on location change
        fusedLocationProviderClient
            .requestLocationUpdates(locationRequest, listener, Looper.getMainLooper())
            .addOnFailureListener { // failed to get location
                locationStateLiveData.value = LocationState.LocationFetchFailure(it)
            }
    }
}
