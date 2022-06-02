package com.musala.weatherApp.domain.usecase

import com.musala.weatherApp.domain.error.ApiErrorManager

/**
 * Executes business logic synchronously or asynchronously using Coroutines.
 */
abstract class BaseUseCase<in P, out R>(private val errorManager: ApiErrorManager) {

    /** Executes the use case asynchronously and returns a [Result].
     *
     * @return a [Result].
     *
     * @param parameters the input parameters to run the use case with
     */
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            Result.Success(execute(parameters))
        } catch (e: Exception) {
            errorManager.catch(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}
