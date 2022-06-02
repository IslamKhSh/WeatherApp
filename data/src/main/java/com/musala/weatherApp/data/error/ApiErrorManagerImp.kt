package com.musala.weatherApp.data.error

import com.musala.weatherApp.domain.usecase.ErrorType
import com.musala.weatherApp.domain.usecase.Result
import com.musala.weatherApp.domain.error.ApiErrorManager
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject


class ApiErrorManagerImp @Inject constructor() : ApiErrorManager {

    override fun catch(throwable: Throwable): Result.Error {
        return when (throwable) {
            is HttpException -> getNetworkingError(throwable)
            is SocketTimeoutException -> Result.Error(ErrorType.TimeOut)
            is IOException -> Result.Error(ErrorType.InternetConnection)
            else -> Result.Error(ErrorType.Other)
        }
    }

    @Suppress("MagicNumber")
    private fun getNetworkingError(httpException: HttpException): Result.Error {
        return when (httpException.code()) {
            401 -> Result.Error(ErrorType.UnAuthorized)
            404 -> Result.Error(ErrorType.NotFound)
            500 -> Result.Error(ErrorType.ServerError)
            else -> Result.Error(ErrorType.Other)
        }
    }
}