package com.wakeupdev.weatherforecast.presentation

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.fragment.app.commit
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.ActivityWeatherBinding
import com.wakeupdev.weatherforecast.presentation.fragments.WeatherFragment
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

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, WeatherFragment())
            }
        }    }

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
}
