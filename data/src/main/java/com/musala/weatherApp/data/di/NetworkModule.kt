package com.musala.weatherApp.data.di

import android.content.Context
import com.musala.weatherApp.data.remote.ApiService
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.musala.weatherApp.data.BuildConfig
import com.musala.weatherApp.data.BuildConfig.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TIMEOUT_SECONDS = 60L

    /**
     * provide Http Interceptor to be used in logging networking
     *
     * @return an instance of Http Interceptor with custom logging
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor().apply { level = BODY }

    /**
     * provide chucker Interceptor to be used in logging networking on devices
     *
     * @return an instance of checker Interceptor
     */
    @Provides
    @Singleton
    fun provideChuckerInterceptor(@ApplicationContext context: Context) =
        ChuckerInterceptor.Builder(context).build()

    /**
     * Provides the okHttp client for networking
     *
     * @param loggingInterceptor the okHttp logging interceptor
     * @param chuckerInterceptor the chucker interceptor to help debugging the api requests on device
     * @return okHttp client instance
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        val client = OkHttpClient.Builder()

        client
            .addInterceptor(chuckerInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, SECONDS) // connect timeout
            .readTimeout(TIMEOUT_SECONDS, SECONDS) // socket timeout
            .writeTimeout(TIMEOUT_SECONDS, SECONDS) // request timeout

        if (BuildConfig.DEBUG)
            client.addInterceptor(loggingInterceptor)

        return client.build()
    }

    /**
     * Provides the Retrofit instance with [BASE_URL]
     *
     * @param httpClient the okHttp client
     * @return the Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofitInterface(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    /**
     * Provides the service implementation
     *
     * @param retrofit the Retrofit object used to instantiate the service
     * @return the ApiService implementation.
     */
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
