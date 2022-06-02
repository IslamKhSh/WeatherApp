package com.musala.weatherApp.core.base

/**
 * Use this interface as a contract for all view base components:
 * [BaseActivity], [BaseFragment], BaseDialogFragment, BaseBottomSheet, and so on.
 */
interface BaseView {

    /**
     * Use it to provide the view layout.
     */
    val layoutRes: Int

    /**
     * Use it to set dataBinding lifeCycleOwner
     * that's because using LiveData in dataBinding requires to be aware of LifeCycle
     */
    fun initBindingLifeCycleOwner()

    /**
     * Use it to init views in your activities and fragments
     */
    fun init()
}
