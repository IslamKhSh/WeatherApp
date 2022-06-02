package com.musala.weatherApp.core.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.toast(res: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, res, duration).show()
}

fun Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, text, duration).show()
}

fun Fragment.toast(res: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, res, duration).show()
}