package com.wakeupdev.weatherforecast.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.wakeupdev.weatherforecast.LocationHelper
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.databinding.FragmentWeatherBinding
import com.wakeupdev.weatherforecast.ui.WeatherUiState
import com.wakeupdev.weatherforecast.ui.adapters.WeatherHourlyAdapter
import com.wakeupdev.weatherforecast.ui.viewmodels.CityViewModel
import com.wakeupdev.weatherforecast.ui.viewmodels.WeatherViewModel
import com.wakeupdev.weatherforecast.utils.ChartHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class WeatherDialogFragment : DialogFragment(R.layout.fragment_weather) {

    companion object {
        private const val ARG_CITY = "arg_city"
        private const val ARG_CITY_NAME = "arg_city_name"

        fun newInstance(city: City, cityName: String): WeatherDialogFragment {
            val fragment = WeatherDialogFragment()
            val args = Bundle().apply {
                putParcelable(ARG_CITY, city)
                putString(ARG_CITY_NAME, cityName)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var binding: FragmentWeatherBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val cityViewModel: CityViewModel by viewModels()
    private lateinit var locationHelper: LocationHelper
    private lateinit var lineChart: LineChart
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeRunnable: Runnable
    private lateinit var weatherDataList: WeatherData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWeatherBinding.bind(view)
        locationHelper = LocationHelper(requireContext())
        lineChart = binding.temperatureChart

        // Retrieve arguments passed to the fragment
        val city = arguments?.getParcelable<City>(ARG_CITY)
        val lat = city?.latitude ?: 0.0
        val lon = city?.longitude ?: 0.0
        val cityName = city?.name ?: "Unknown City"

        Log.d("WeatherDialogFragment", "onViewCreated: $arguments")

        // Show weather data for the passed location
        fetchWeatherForCity(lat, lon, cityName)

        binding.rlFavorite.visibility = View.VISIBLE

        binding.tvCancel.setOnClickListener { dismiss() }

        binding.tvAddFav.setOnClickListener {
            lifecycleScope.launch {
                if (city != null) {
                    if (cityViewModel.saveFavoriteCity(
                            city
                        ) > 0){
                        Toast.makeText(requireContext(), "successfully added to favorites", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
        }

        binding.btnNext7days.setOnClickListener {

        }

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        timeRunnable = object : Runnable {
            override fun run() {
                // Update the time dynamically
                val currentTime = timeFormat.format(Date())
                binding.tvTodayTime.text = "Today $currentTime"

                // Schedule the next update in 1 minute
                handler.postDelayed(this, 60000)
            }
        }

        // Start updating the time
        handler.post(timeRunnable)

        observeUiState()
    }

    private fun fetchWeatherForCity(lat: Double, lon: Double, cityName: String) {
        weatherViewModel.fetchWeatherForCity(lat, lon)
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

                        is WeatherUiState.Error -> {}

                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun updateWeatherDetails(it: WeatherData) {
        binding.btnNext7days.isEnabled = true
        weatherDataList = it
        binding.tvCityName.text = arguments?.getString(ARG_CITY_NAME) ?: it.cityName?.split("/")?.get(1) ?: ""
        binding.tvCurrentTemperature.text =
            getString(R.string.current_temperature, it.currentTemperature)

        ChartHelper.updateLineChart(requireContext(), lineChart, it.hourlyTemperature)

        binding.tvPressure.text = getString(R.string.pressure_hpa, it.pressure)
        binding.tvWeatherCondition.text = it.weatherCondition
        binding.tvMinTemp.text = getString(R.string.min_temp, it.minTemperature)
        binding.tvMaxTemp.text = getString(R.string.max_temp, it.maxTemperature)
        binding.tvWindSpeed.text = getString(R.string.wind_m_s, it.windSpeed)
        binding.tvHumidity.text = getString(R.string.humidity, it.humidity)

        // Set up daily forecast RecyclerView
        binding.rvHourlyForecast.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHourlyForecast.adapter =
            WeatherHourlyAdapter(it.hourlyTemperature, requireContext())
    }
}
