package com.wakeupdev.weatherforecast.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.wakeupdev.weatherforecast.Constants
import com.wakeupdev.weatherforecast.LocationHelper
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.databinding.FragmentWeatherBinding
import com.wakeupdev.weatherforecast.ui.WeatherUiState
import com.wakeupdev.weatherforecast.ui.adapters.WeatherHourlyAdapter
import com.wakeupdev.weatherforecast.ui.viewmodels.WeatherViewModel
import com.wakeupdev.weatherforecast.utils.ChartHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.fragment_weather) {
    private lateinit var binding: FragmentWeatherBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var locationHelper: LocationHelper
    private lateinit var lineChart: LineChart
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeRunnable: Runnable
    private lateinit var weatherDataList: WeatherData
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var citName: String = ""

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Log.d("WeatherFragment", "Permissions granted. Fetching weather...")
            fetchWeatherForCurrentLocation()
        } else {
            showPermissionDeniedMessage()
        }
    }

    companion object {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherBinding.bind(view)
        locationHelper = LocationHelper(requireContext())
        lineChart = binding.temperatureChart

        arguments?.let {
            Log.d("WeatherFragment", "onViewCreated: $arguments")
            latitude = it.getDouble("lat", 0.0)
            longitude = it.getDouble("lon", 0.0)
            citName = it.getString("city_name", "")
        }

        binding.btnNext7days.setOnClickListener {
            val dailyWeatherFragment = DailyWeatherFragment()
            val bundle = Bundle()
            bundle.putParcelable("weatherData", weatherDataList)
            dailyWeatherFragment.arguments = bundle

            // Ensure the fragment is added to the back stack
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, dailyWeatherFragment)
                addToBackStack(null)
            }
        }




        binding.imgFavCities.setOnClickListener {
            val favoriteCitiesFragment = FavoriteCitiesFragment()

            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, favoriteCitiesFragment)
                addToBackStack(null)
            }
        }

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        timeRunnable = object : Runnable {
            override fun run() {
                val currentTime = timeFormat.format(Date())
                binding.tvTodayTime.text = "Today $currentTime"
                handler.postDelayed(this, 60000)
            }
        }
        handler.post(timeRunnable)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchWeatherForCurrentLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        observeUiState()
    }


    private fun fetchWeatherForCurrentLocation() {
        lifecycleScope.launch {
            // If latitude and longitude are available (either passed or fallback), fetch weather
            if (latitude != 0.0 && longitude != 0.0 && citName.isNotEmpty()) {
                weatherViewModel.fetchWeatherForCity(latitude, longitude, citName, false)
            } else {
                // Otherwise, get the current location
                val location = locationHelper.getCurrentLocation()

                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    weatherViewModel.fetchWeatherForCity(lat, lon, "")
                } else {
                    // Handle case when location is not available
                    showPermissionDeniedMessage() // You can show a dialog or toast here to prompt the user for location permissions
                }
            }
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(requireContext(), "Location permission is required.", Toast.LENGTH_SHORT).show()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weatherState.collect { weatherState ->
                    when (weatherState) {
                        is WeatherUiState.Loading -> {}

                        is WeatherUiState.Idle -> {}

                        is WeatherUiState.Success -> {
                            updateWeatherDetails(weatherState.weatherData)
                        }

                        is WeatherUiState.Error -> {
                            // Handle error state
                        }
                    }
                }
            }
        }
    }

    private fun updateWeatherDetails(weatherData: WeatherData) {
        binding.btnNext7days.isEnabled = true
        weatherDataList = weatherData

        binding.tvCityName.text = weatherData.cityName?.split("/")?.get(1) ?: ""
        binding.tvCurrentTemperature.text = getString(R.string.current_temperature, weatherData.currentTemperature)

        // Update line chart using ChartHelper
        ChartHelper.updateLineChart(requireContext(), lineChart, weatherData.hourlyTemperature)

        // Update other weather details
        binding.tvPressure.text = getString(R.string.pressure_hpa, weatherData.pressure)
        binding.tvWeatherCondition.text = weatherData.weatherCondition
        binding.tvMinTemp.text = getString(R.string.min_temp, weatherData.minTemperature)
        binding.tvMaxTemp.text = getString(R.string.max_temp, weatherData.maxTemperature)
        binding.tvWindSpeed.text = getString(R.string.wind_m_s, weatherData.windSpeed)
        binding.tvHumidity.text = getString(R.string.humidity, weatherData.humidity)

        // Update hourly forecast RecyclerView
        binding.rvHourlyForecast.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHourlyForecast.adapter = WeatherHourlyAdapter(weatherData.hourlyTemperature, requireContext())
    }
}
