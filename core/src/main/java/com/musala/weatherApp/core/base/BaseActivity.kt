package com.musala.weatherApp.core.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * base activity that all activities must extent
 *
 * @param DB the view binding of activity layout
 */
abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity(), BaseView {

    /**
     * The view binding of the activity layout
     */
    protected val binding by lazy { DataBindingUtil.setContentView(this, layoutRes) as DB }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBindingLifeCycleOwner()
        init()
    }

    override fun initBindingLifeCycleOwner() {
        binding.lifecycleOwner = this
    }
}
