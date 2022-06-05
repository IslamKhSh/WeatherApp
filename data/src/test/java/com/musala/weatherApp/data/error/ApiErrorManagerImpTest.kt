package com.musala.weatherApp.data.error

import com.musala.weatherApp.domain.entity.ErrorType
import io.mockk.junit5.MockKExtension
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
internal class ApiErrorManagerImpTest {

    private lateinit var errManager: ApiErrorManagerImp

    @BeforeEach
    fun setUp() {
        errManager = ApiErrorManagerImp()
    }

    @ParameterizedTest
    @MethodSource("typeOfErrors")
    fun `given different types of throwable - call catch - then return proper errorType`(
        throwable: Throwable,
        errorType: ErrorType
    ) {
        // given in params

        // when
        val result = errManager.catch(throwable)

        // then
        result.errorType shouldBeEqualTo errorType
    }

    companion object {
        @JvmStatic
        @Suppress("UnusedPrivateMember")
        private fun typeOfErrors(): Stream<Arguments?>? {
            return Stream.of(
                arguments(SocketTimeoutException(), ErrorType.TimeOut),
                arguments(IOException(), ErrorType.InternetConnection),
                arguments(UnknownError(), ErrorType.Other)
            )
        }
    }
}