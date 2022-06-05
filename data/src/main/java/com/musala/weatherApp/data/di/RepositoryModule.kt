package com.musala.weatherApp.data.di

import com.musala.weatherApp.data.repository.WeatherRepoImp
import com.musala.weatherApp.domain.repository.WeatherRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * binds the implementation of repositories (in data layer) to their abstractions (in domain layer)
 * so when a use case ask for an abstract repository hilt will provide the implementation of it
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepo(repo : WeatherRepoImp) : WeatherRepo

}