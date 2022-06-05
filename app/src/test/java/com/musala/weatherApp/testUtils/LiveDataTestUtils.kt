package com.musala.weatherApp.testUtils

import androidx.lifecycle.Observer
import io.mockk.clearMocks
import io.mockk.spyk
import io.mockk.verify

inline fun <reified T : Any> getLiveDataChanges(observer: Observer<T>): List<T> {
    val changes = mutableListOf<T>()
    verify { observer.onChanged(capture(changes)) }
    clearMocks(observer)
    return changes
}

@Suppress("ObjectLiteralToLambda")
fun <T : Any> createMockedObserver(): Observer<T> {
    val observer = object : Observer<T> {
        override fun onChanged(t: T?) {
            println("new value observed: $t")
        }
    }
    return spyk<Observer<T>>(observer)
}
