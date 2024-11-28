package com.wakeupdev.weatherforecast.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.FragmentDailyWeatherBinding
import com.wakeupdev.weatherforecast.data.db.WeatherData
import com.wakeupdev.weatherforecast.Constants.ARG_WEATHER_DATA
import com.wakeupdev.weatherforecast.presentation.adapters.WeatherDailyAdapter
import com.wakeupdev.weatherforecast.presentation.adapters.WeatherHourlyAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyWeatherFragment : Fragment(R.layout.fragment_daily_weather) {
    private lateinit var binding: FragmentDailyWeatherBinding
    private lateinit var weatherData: WeatherData // Store the passed data

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDailyWeatherBinding.bind(view)

        // Retrieve the Parcelable data
        weatherData = arguments?.getParcelable(ARG_WEATHER_DATA)
            ?: throw IllegalArgumentException("WeatherData is required")

        binding.tvCityName.text = weatherData.cityName

        binding.imgNavBack.setOnClickListener { parentFragmentManager.popBackStack() }

        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        // Hourly Forecast
        binding.rvHourlyForecast.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHourlyForecast.adapter = WeatherHourlyAdapter(weatherData.hourlyTemperature, requireContext())

        // Daily Forecast
        binding.rvDailyForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDailyForecast.adapter = WeatherDailyAdapter(weatherData.dailyForecast, requireContext())
    }

    companion object {
        fun newInstance(weatherData: WeatherData): DailyWeatherFragment {
            val fragment = DailyWeatherFragment()
            val args = Bundle().apply {
                putParcelable(ARG_WEATHER_DATA, weatherData)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
