package com.wakeupdev.weatherforecast.features.weather.presentation

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.FragmentDailyWeatherBinding
import com.wakeupdev.weatherforecast.features.weather.data.WeatherData
import com.wakeupdev.weatherforecast.shared.Constants.ARG_WEATHER_DATA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyWeatherFragment : Fragment(R.layout.fragment_daily_weather) {
    private lateinit var binding: FragmentDailyWeatherBinding
    private lateinit var weatherData: WeatherData // Store the passed data

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDailyWeatherBinding.bind(view)

        val args: DailyWeatherFragmentArgs by navArgs()
        weatherData = args.weatherData

        binding.tvCityName.text = weatherData.cityName

        binding.imgNavBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Set up RecyclerViews
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        // Hourly Forecast
        binding.rvHourlyForecast.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHourlyForecast.adapter = WeatherHourlyAdapter(weatherData.hourlyTemperature, requireContext())

        // Daily Forecast
        binding.rvDailyForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDailyForecast.adapter = WeatherDailyAdapter(weatherData.dailyForecast, requireContext())

        // Add Divider to Daily Forecast
//        val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
//        ContextCompat.getDrawable(requireContext(), R.drawable.custom_divider)?.let { drawable ->
//            divider.setDrawable(drawable)
//        }
//        binding.rvDailyForecast.addItemDecoration(divider)
    }

}
