package com.musala.weatherApp.domain.repository

import com.musala.weatherApp.domain.entity.CurrentWeather

interface WeatherRepo {
    suspend fun getCurrentWeather(lat : Double, lng: Double): CurrentWeather
}