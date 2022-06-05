package com.musala.weatherApp.domain.usecase

import com.musala.weatherApp.domain.entity.CurrentWeather
import com.musala.weatherApp.domain.error.ApiErrorManager
import com.musala.weatherApp.domain.repository.WeatherRepo
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    errorManager: ApiErrorManager,
    private val weatherRepo: WeatherRepo
) : BaseUseCase<Pair<Double, Double>, CurrentWeather>(errorManager) {

    override suspend fun execute(parameters: Pair<Double, Double>): CurrentWeather =
        weatherRepo.getCurrentWeather(parameters.first, parameters.second)
}
