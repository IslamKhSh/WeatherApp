package com.musala.weatherApp.ui


import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.musala.weatherApp.R
import com.musala.weatherApp.core.base.BaseActivity
import com.musala.weatherApp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutRes: Int = R.layout.activity_main

    private lateinit var navController: NavController

    override fun init() {
        navController = findNavController(R.id.nav_host_fragment)
    }

    /**
     * when user click on up button navigate up.
     */
    override fun onSupportNavigateUp() = navController.navigateUp()
}