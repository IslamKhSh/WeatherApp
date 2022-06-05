package com.musala.weatherApp.di

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeocoderModule {

    @Provides
    @Singleton
    fun provideGeocoderInstance(@ApplicationContext context: Context) = Geocoder(context)
}
