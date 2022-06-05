package com.musala.weatherApp.domain.entity

const val TEMPERATURE = 30
const val MIN_TEMP = 25
const val MAX_TEMP = 35
const val TEMP_FEELS_LIKE = 31
const val CLOUDS = 60
const val WIND_M_S = 1000.0
const val WIND_KM_H = 3600.0
const val HUMIDITY = 50
const val DESCRIPTION = "description"
const val ICON = "icon"
const val ICON_FULL_URL = "https://openweathermap.org/img/wn/$ICON@4x.png"

val currentWeatherFixture = CurrentWeather(
    temperature = TEMPERATURE,
    tempMax = MAX_TEMP,
    tempMin = MIN_TEMP,
    tempFeelsLike = TEMP_FEELS_LIKE,
    clouds = CLOUDS,
    humidity = HUMIDITY,
    windSpeed = WIND_KM_H,
    description = DESCRIPTION,
    icon = ICON_FULL_URL
)