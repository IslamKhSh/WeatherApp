package com.musala.weatherApp.domain.error

import com.musala.weatherApp.domain.entity.Result

/**
 * detect the error type and return Result.Error
 */
interface ApiErrorManager {
    fun catch(throwable: Throwable): Result.Error
}
