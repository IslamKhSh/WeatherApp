package com.musala.weatherApp.core.extensions

fun Collection<Boolean>.isAllTrue() = !this.contains(false)
