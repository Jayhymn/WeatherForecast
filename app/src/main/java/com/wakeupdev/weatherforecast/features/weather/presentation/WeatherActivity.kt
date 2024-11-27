package com.wakeupdev.weatherforecast.features.weather.presentation

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.ActivityWeatherBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            animateSplashScreen()
        }

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the navigation
        setupNavigation()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun animateSplashScreen() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Delay the exit animation
            splashScreenView.postDelayed({
                val slideLeft = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_X,
                    0f,
                    -splashScreenView.width.toFloat()
                )
                slideLeft.interpolator = AnticipateInterpolator()
                slideLeft.duration = 400L

                slideLeft.doOnEnd { splashScreenView.remove() }
                slideLeft.start()
            }, 1500) // Reduced delay to 1.5 seconds for better UX
        }
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.fragmentContainer)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
