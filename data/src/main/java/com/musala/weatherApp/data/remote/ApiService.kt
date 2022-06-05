package com.musala.weatherApp.data.remote

import com.musala.weatherApp.data.entities.WeatherEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lng: Double,
        @Query("units") unit: String = "metric"
    ): WeatherEntity
}
