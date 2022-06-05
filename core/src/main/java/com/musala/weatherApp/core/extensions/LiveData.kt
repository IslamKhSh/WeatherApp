package com.musala.weatherApp.core.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

val <T> MutableLiveData<T>.asLiveData
    get() = this as LiveData<T>
