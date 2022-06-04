package com.musala.weatherApp.features.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.musala.weatherApp.R
import com.musala.weatherApp.core.base.BaseFragment
import com.musala.weatherApp.databinding.FragmentWeatherBinding
import com.musala.weatherApp.utils.LocationManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment :
    BaseFragment<FragmentWeatherBinding, WeatherViewState, WeatherViewModel>() {

    override val layoutRes: Int = R.layout.fragment_weather

    private val locationManager by lazy { LocationManager(requireContext(), this) }
    private val autocompleteFragment by lazy {
        childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
    }

    override fun getViewModelClass(): Class<WeatherViewModel> = WeatherViewModel::class.java

    override fun renderViewState(state: WeatherViewState) {
        when (state) {
            WeatherViewState.FetchingCurrentLocation ->
                renderPlaceholderView(R.string.fetching_current_location, R.drawable.ic_fetching_location)
            WeatherViewState.FetchLocationFailed ->
                renderPlaceholderView(R.string.fetching_location_error, R.drawable.ic_location_error)
            is WeatherViewState.FetchingPlaceWeather -> {
                autocompleteFragment.setText(state.cityName)
                binding.flipperContent.displayedChild = binding.flipperContent.indexOfChild(binding.viewLoading)
            }
        }
    }

    private fun renderPlaceholderView(@StringRes textRes: Int, @DrawableRes imageRes: Int) {
        binding.flipperContent.displayedChild = binding.flipperContent.indexOfChild(binding.viewPlaceholder.root)
        binding.text = getString(textRes)
        binding.image = getDrawable(requireContext(), imageRes)
    }

    override fun init() {
        locationManager.getCurrentLocation()
            .observe(viewLifecycleOwner, viewModel::onLocationStateUpdated)
        initPlacesSearch()
    }

    private fun initPlacesSearch() {
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                viewModel.onPlaceSelected(place)
            }

            override fun onError(status: Status) {
            }
        })

    }

}