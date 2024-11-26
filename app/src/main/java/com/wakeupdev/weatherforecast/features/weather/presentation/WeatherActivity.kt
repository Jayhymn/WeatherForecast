package com.wakeupdev.weatherforecast.features.weather.presentation

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.ActivityWeatherBinding
import com.wakeupdev.weatherforecast.shared.Constants
import com.wakeupdev.weatherforecast.shared.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            animateSplashScreen()
        }

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        locationHelper = LocationHelper(this)

        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weatherState.collect { weatherState ->
                    when (weatherState) {
                        is WeatherUiState.Loading -> {}

                        is WeatherUiState.Idle -> {}

                        is WeatherUiState.Success -> {
                            weatherState.weatherData.let {
                                binding.tvCityName.text = it.cityName
                                binding.tvCurrentTemperature.text =
                                    getString(R.string.current_temperature, it.currentTemperature)

                                binding.tvMinMaxTemperature.text = getString(
                                    R.string.min_to_max_temp,
                                    it.minTemperature,
                                    it.maxTemperature
                                )
                                binding.tvWeatherCondition.text = it.weatherCondition

                                setWeatherIcon(it.weatherIcon)

                                // Set up daily forecast RecyclerView
                                binding.rvDailyForecast.layoutManager = LinearLayoutManager(this@WeatherActivity)
                                binding.rvDailyForecast.adapter =
                                    WeatherDetailsAdapter(it.dailyForecast, this@WeatherActivity)

                                val divider = DividerItemDecoration(this@WeatherActivity, LinearLayoutManager.VERTICAL)
                                ContextCompat.getDrawable(this@WeatherActivity, R.drawable.custom_divider)?.let { drawable ->
                                    divider.setDrawable(drawable)
                                }

                                binding.rvDailyForecast.addItemDecoration(divider)
                            }
                        }

                        is WeatherUiState.Error -> {}
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    // this is used for animating the transition from the splashscreen to this activity
    private fun animateSplashScreen() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Delay the exit animation to ensure the splash screen is displayed for the full duration
            splashScreenView.postDelayed({
                val slideLeft = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_X,
                    0f,
                    -splashScreenView.width.toFloat()
                )

                slideLeft.interpolator = AnticipateInterpolator()
                slideLeft.duration = 400L

                // Remove the splash screen view after the animation ends
                slideLeft.doOnEnd { splashScreenView.remove() }

                slideLeft.start() // Start the slide animation
            }, 3000) // Delay before starting the exit animation
        }
    }

    private suspend fun fetchWeatherForCurrentLocation() {
        // Get current location (latitude and longitude)
        val location = locationHelper.getCurrentLocation()

        // If location is available, fetch weather data
        location?.let {
            val lat = it.latitude
            val lon = it.longitude
            val cityName = "Current City" // You can update this based on reverse geocoding if needed

            // Pass the location data to ViewModel to fetch weather
            weatherViewModel.fetchWeatherForCity(lat, lon, cityName)
        } ?: run {
            // Handle case where location is not available (show an error or fallback message)
        }
    }

    private fun setWeatherIcon(iconId: String) {
        val iconUrl = "${Constants.ICON_URL}$iconId@2x.png"

        Glide.with(this)
            .load(iconUrl) // Load the icon URL
            .error(R.drawable.ic_clear_sky)
            .into(binding.ivWeatherIcon) // Set it into ImageView
    }
}