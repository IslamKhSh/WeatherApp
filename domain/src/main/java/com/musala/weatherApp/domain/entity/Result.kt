package com.musala.weatherApp.domain.entity

import com.musala.weatherApp.domain.entity.Result.Error
import com.musala.weatherApp.domain.entity.Result.Success

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val errorType: ErrorType, val errorMessage: String? = null) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[type=$errorType, message=$errorMessage]"
        }
    }
}

sealed class ErrorType {

    object UnAuthorized : ErrorType() // when error code 401
    object NotFound : ErrorType() // when error code 404
    object ServerError : ErrorType() // when error code 500

    object InternetConnection : ErrorType() // when IOException
    object TimeOut : ErrorType() // when SocketTimeoutException

    object Other : ErrorType() // when not defined
}

/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val Result<*>.isSucceeded
    get() = this is Success && data != null

val <T> Result<T>.data: T
    get() = (this as Success).data!!

val <T> Result<T>.error: Error?
    get() = this as? Error
