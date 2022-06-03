package com.musala.weatherApp.features.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.musala.weatherApp.R
import com.musala.weatherApp.core.base.BaseFragment
import com.musala.weatherApp.databinding.FragmentWeatherBinding
import com.musala.weatherApp.utils.LocationManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment :
    BaseFragment<FragmentWeatherBinding, WeatherViewStates, WeatherViewModel>() {

    override val layoutRes: Int = R.layout.fragment_weather

    private val locationManager by lazy { LocationManager(requireContext(), this) }

    override fun getViewModelClass(): Class<WeatherViewModel> = WeatherViewModel::class.java

    override fun renderViewState(state: WeatherViewStates) {
        when(state){
            WeatherViewStates.FetchingCurrentLocation ->
                renderPlaceholderView(R.string.fetching_current_location, R.drawable.ic_fetching_location)
            WeatherViewStates.FetchLocationFailed ->
                renderPlaceholderView(R.string.fetching_location_error, R.drawable.ic_location_error)

        }
    }

    private fun renderPlaceholderView(@StringRes textRes : Int, @DrawableRes imageRes : Int){
        binding.text = getString(textRes)
        binding.image = getDrawable(requireContext(), imageRes)
    }

    override fun init() {
        locationManager.getCurrentLocation()
            .observe(viewLifecycleOwner, viewModel::onLocationStateUpdated)
    }
}