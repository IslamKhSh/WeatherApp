package com.musala.weatherApp.core.extensions

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * Extension function to create explicit intent to open activity
 *
 * @receiver Activity
 * @param targetActivity Class<out Activity> the class of activity to navigate to it.
 * @param finishCurrent Boolean true if current activity must be destroyed, false otherwise.
 */
fun Activity.openActivity(
    targetActivity: Class<out Activity>,
    finishCurrent: Boolean = false
) {
    startActivity(Intent(this, targetActivity))

    if (finishCurrent) {
        finish()
    }
}

/**
 * Extension function to create explicit intent to open activity.
 *
 * @receiver Fragment
 * @param targetActivity Class<out Activity> the class of activity to navigate to it.
 * @param finishCurrent Boolean true if current activity must be destroyed, false otherwise.
 */
fun Fragment.openActivity(
    targetActivity: Class<out Activity>,
    finishCurrent: Boolean = false
) {
    activity?.openActivity(targetActivity, finishCurrent)
}
