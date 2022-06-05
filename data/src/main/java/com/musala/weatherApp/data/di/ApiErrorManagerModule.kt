package com.musala.weatherApp.data.di

import com.musala.weatherApp.data.error.ApiErrorManagerImp
import com.musala.weatherApp.domain.error.ApiErrorManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * binds the [ApiErrorManagerImp] the implementation (in data layer)
 * to its abstractions [ApiErrorManager] (in domain layer).
 *
 * so when a use case ask for an abstract [ApiErrorManager]
 * hilt will provide the implementation of it
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ApiErrorManagerModule {

    @Binds
    abstract fun bindErrorManager(errorManagerImp: ApiErrorManagerImp): ApiErrorManager
}
