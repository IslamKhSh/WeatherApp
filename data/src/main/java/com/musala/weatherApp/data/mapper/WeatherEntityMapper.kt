package com.musala.weatherApp.data.mapper

import androidx.annotation.VisibleForTesting
import com.musala.weatherApp.data.BuildConfig
import com.musala.weatherApp.data.entities.WeatherEntity
import com.musala.weatherApp.domain.entity.CurrentWeather
import javax.inject.Inject

class WeatherEntityMapper @Inject constructor() : Mapper<WeatherEntity, CurrentWeather> {

    override fun WeatherEntity.map() = CurrentWeather(
        temperature = main.temp.toInt(),
        tempFeelsLike = main.feelsLike.toInt(),
        tempMin = main.tempMin.toInt(),
        tempMax = main.tempMax.toInt(),
        humidity = main.humidity,
        clouds = clouds.all,
        windSpeed = wind.speed.toKmPerHour(),
        description = weather.firstOrNull()?.description,
        icon = weather.firstOrNull()?.icon?.let { BuildConfig.WEATHER_ICON_URL.format(it) }
    )

    /**
     * convert wind unit from m/s to km/h
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun Double.toKmPerHour() = this * 60 * 60 / 1000
}