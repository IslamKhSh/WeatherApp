package com.musala.weatherApp.data.repository

import com.musala.weatherApp.data.entities.WeatherEntity
import com.musala.weatherApp.data.mapper.Mapper
import com.musala.weatherApp.data.mapper.WeatherEntityMapper
import com.musala.weatherApp.data.remote.ApiService
import com.musala.weatherApp.domain.entity.CurrentWeather
import com.musala.weatherApp.domain.repository.WeatherRepo
import javax.inject.Inject

class WeatherRepoImp @Inject constructor(
    private val apiService: ApiService,
    private val weatherMapper: WeatherEntityMapper
) : WeatherRepo, Mapper<WeatherEntity, CurrentWeather> by weatherMapper {

    override suspend fun getCurrentWeather(lat: Double, lng: Double): CurrentWeather {
        return apiService.getCurrentWeather(lat, lng).map()
    }
}
