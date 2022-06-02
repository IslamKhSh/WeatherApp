package com.musala.weatherApp.data.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * binds the implementation of repositories (in data layer) to their abstractions (in domain layer)
 * so when a use case ask for an abstract repository hilt will provide the implementation of it
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

}