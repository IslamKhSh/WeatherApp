package com.musala.weatherApp.core.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * Extension function to get int value of color from color resource.
 *
 * @receiver Context
 * @param resourceId Int the color res.
 * @return Int value of the color.
 */
fun Context.getColorCompat(@ColorRes resourceId: Int) = ContextCompat.getColor(this, resourceId)

/**
 * Extension function to get drawable from drawable resource.
 *
 * @receiver Context
 * @param resourceId Int the drawable res
 * @return Drawable? the instance of drawable
 */
fun Context.getDrawableCompat(@DrawableRes resourceId: Int) =
    ContextCompat.getDrawable(this, resourceId)
