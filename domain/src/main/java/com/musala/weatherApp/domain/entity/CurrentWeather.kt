package com.musala.weatherApp.domain.entity

data class CurrentWeather(
    val temperature: Int,
    val tempFeelsLike: Int,
    val tempMin: Int,
    val tempMax: Int,
    val humidity: Int,
    val clouds: Int,
    val windSpeed: Double,
    val description: String?,
    val icon: String?
)
