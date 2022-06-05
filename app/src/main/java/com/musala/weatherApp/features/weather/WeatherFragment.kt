package com.musala.weatherApp.features.weather

import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isVisible
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.musala.weatherApp.R
import com.musala.weatherApp.core.base.BaseFragment
import com.musala.weatherApp.databinding.FragmentWeatherBinding
import com.musala.weatherApp.domain.entity.CurrentWeather
import com.musala.weatherApp.utils.LocationManager
import com.musala.weatherApp.utils.displayView
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
                renderPlaceholderView(
                    R.string.fetching_current_location,
                    R.drawable.ic_fetching_location
                )
            WeatherViewState.WaitingSearchInput ->
                renderPlaceholderView(R.string.search_for_city, R.drawable.ic_search)
            is WeatherViewState.FetchingPlaceWeather -> {
                autocompleteFragment.setText(state.cityName)
                binding.flipperContent.displayedChild =
                    binding.flipperContent.indexOfChild(binding.viewLoading)
            }
            is WeatherViewState.DisplayCurrentWeather -> renderWeatherView(
                state.cityName,
                state.currentWeather
            )
            is WeatherViewState.FetchingCurrentWeatherError -> renderErrorView(
                R.string.something_wrong,
                R.drawable.ic_error,
                state.cityName,
                state.latLng
            )
            is WeatherViewState.InternetConnectionError -> renderErrorView(
                R.string.no_internet_connection,
                R.drawable.ic_connection_error,
                state.cityName,
                state.latLng
            )
        }
    }

    private fun renderPlaceholderView(@StringRes textRes: Int, @DrawableRes imageRes: Int) {
        binding.flipperContent.displayView(binding.viewPlaceholder.root)
        binding.text = getString(textRes)
        binding.image = getDrawable(requireContext(), imageRes)
    }

    private fun renderErrorView(
        @StringRes textRes: Int,
        @DrawableRes imageRes: Int,
        cityName: String,
        latLng: LatLng
    ) {
        autocompleteFragment.setText(cityName)
        binding.flipperContent.displayView(binding.viewError.root)
        binding.text = getString(textRes)
        binding.image = getDrawable(requireContext(), imageRes)
        binding.viewError.btnRetry.setOnClickListener {
            viewModel.getCurrentWeather(cityName, latLng)
        }
    }

    private fun renderWeatherView(cityName: String, currentWeather: CurrentWeather) {
        autocompleteFragment.setText(cityName)
        binding.weather = currentWeather
        binding.flipperContent.displayView(binding.currentWeatherView.root)
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
        initAutocompleteCloseBtn()
    }

    private fun initAutocompleteCloseBtn() {
        view?.post {
            val closeBtn =
                (autocompleteFragment.view as? ViewGroup)?.findViewById<View>(R.id.places_autocomplete_clear_button)

            closeBtn?.setOnClickListener {
                autocompleteFragment.setText("")
                it.isVisible = false
                viewModel.clearSelectedPlace()
            } ?: initAutocompleteCloseBtn()
        }
    }
}