package com.musala.weatherApp.core.extensions

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(message: String, length: Int = LENGTH_SHORT) =
    Snackbar.make(this, message, length).show()