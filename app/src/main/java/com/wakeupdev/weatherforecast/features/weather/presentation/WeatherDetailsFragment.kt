package com.wakeupdev.weatherforecast.features.weather.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.FragmentWeatherDetailsBinding
import com.wakeupdev.weatherforecast.shared.Constants
import com.wakeupdev.weatherforecast.shared.LocationHelper
import kotlinx.coroutines.launch

class WeatherDetailsFragment : Fragment() {

    private lateinit var binding: FragmentWeatherDetailsBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var locationHelper: LocationHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        locationHelper = LocationHelper(requireContext())

        // Use viewLifecycleOwner.lifecycleScope instead of lifecycleScope
        viewLifecycleOwner.lifecycleScope.launch {
            // This ensures that the flow collection starts and stops correctly with the fragment's view lifecycle
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weatherState.collect { weather ->
                    weather.weatherData?.let {
                        binding.tvCityName.text = it.cityName
                        binding.tvCurrentTemperature.text = getString(R.string.current_temperature, it.currentTemperature)

                        binding.tvMinMaxTemperature.text = getString(
                            R.string.min_to_max_temp,
                            it.minTemperature,
                            it.maxTemperature
                        )
                        binding.tvWeatherCondition.text = it.weatherCondition

                        setWeatherIcon(it.weatherIcon)

                        // Set up daily forecast RecyclerView
                        binding.rvDailyForecast.layoutManager = LinearLayoutManager(context)
                        binding.rvDailyForecast.adapter = WeatherDetailsAdapter(it.dailyForecast, requireContext())
                    }
                }
            }
        }

        return binding.root
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

        Glide.with(requireContext())
            .load(iconUrl) // Load the icon URL
            .error(R.drawable.ic_clear_sky)
            .into(binding.ivWeatherIcon) // Set it into ImageView
    }

}