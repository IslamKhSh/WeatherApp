package com.musala.weatherApp.core.binding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.request.CachePolicy

object BindingAdapters {

    /**
     * Extension function to set img to [ImageView] from url using [Coil](https://github.com/coil-kt/coil).
     *
     * @receiver [ImageView]
     * @param url String? the url of the img
     * @param placeHolder Drawable? the img to be displayed until the img loaded or if the loading failed
     */
    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "placeHolder"], requireAll = false)
    fun ImageView.bindImage(
        url: String?,
        placeHolder: Drawable?
    ) {
        load(url) {
            placeholder(placeHolder)
            crossfade(true)
            error(placeHolder)
            fallback(placeHolder)
            diskCachePolicy(CachePolicy.ENABLED)
        }
    }
}
