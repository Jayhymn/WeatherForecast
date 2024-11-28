package com.wakeupdev.weatherforecast.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.FragmentWeatherBinding
import com.wakeupdev.weatherforecast.data.HourlyTemperature
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.Constants
import com.wakeupdev.weatherforecast.LocationHelper
import com.wakeupdev.weatherforecast.ui.WeatherUiState
import com.wakeupdev.weatherforecast.ui.viewmodels.WeatherViewModel
import com.wakeupdev.weatherforecast.ui.adapters.WeatherHourlyAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWeatherBinding.bind(view)
        locationHelper = LocationHelper(requireContext())
        lineChart = binding.temperatureChart

        binding.btnNext7days.setOnClickListener {
            val action = WeatherFragmentDirections.actionCurrentFragmentToDailyWeatherFragment(weatherDataList)
            findNavController().navigate(action)
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

        if (hasLocationPermission()) {
            fetchWeather()
        } else {
            requestLocationPermission()
        }

        observeUiState()

    }

    private fun updateLineChart(temperatureData: List<HourlyTemperature>) {
        // Map temperature data to entries
        val entries = temperatureData.take(24).mapIndexed { index, temperature ->
            Entry(index.toFloat(), temperature.temperature!!.toFloat())
        }

        // Create the dataset (LineDataSet)
        val dataSet = LineDataSet(entries, "Hourly Temperature").apply {
            color = resources.getColor(R.color.primary, null)
            valueTextColor = resources.getColor(R.color.textPrimary, null) // Modify this to hide labels
            lineWidth = 2f // Set line width
            setDrawCircles(true) // Draw circles at each point
            circleRadius = 4f // Size of the circles
            setDrawCircleHole(false) // Disable hole in the center of circles
            mode = LineDataSet.Mode.CUBIC_BEZIER // Creates a smooth curved line
            setDrawFilled(true) // Fill the area under the line
            fillColor = resources.getColor(R.color.primary, null) // Fill color
            fillAlpha = 50 // Fill transparency
        }

        // Set up the line chart data
        val lineData = LineData(dataSet)

        // Configure the LineChart
        lineChart.apply {
            data = lineData
            description.isEnabled = false // Disable the description label
            setTouchEnabled(true) // Allow user interactions
            isDragEnabled = true
            setScaleEnabled(true)
            axisLeft.isEnabled = false // Hide the left Y-axis
            axisRight.isEnabled = false // Hide the right Y-axis
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            // Customize X-axis to show time labels in increments of 3
            val timeLabels = temperatureData.take(24).map { it.date } // Assuming 'date' is in a valid format

            // Ensure that we only show every 3rd value
            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    val index = value.toInt()
                    return if (index % 3 == 0) { // Show time every 3rd index
                        timeLabels.getOrNull(index) ?: ""
                    } else {
                        "" // Leave other values blank
                    }
                }
            }

            // Customize Y-axis to hide completely and not show any grid lines
            axisLeft.apply {
                setDrawLabels(false) // Do not draw Y-axis labels
                setDrawGridLines(false) // Do not draw Y-axis grid lines
            }

            // Ensure the X-axis has consistent spacing
            xAxis.granularity = 1f // Each point corresponds to an index
            xAxis.setAvoidFirstLastClipping(true) // Prevent clipping of the first and last labels

            // Customize X-axis labels to prevent overcrowding
            xAxis.labelRotationAngle = -45f // Rotate the labels if needed
        }

        lineChart.invalidate() // Refresh the chart
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with fetching location
                fetchWeather()
            } else {
                // Permission denied, show a fallback or message
                showPermissionDeniedMessage()
            }
        }
    }

    private fun fetchWeather() {
        lifecycleScope.launch {
            fetchWeatherForCurrentLocation()
        }
    }

    private fun showPermissionDeniedMessage() {
        // Show a message or dialog informing the user that location permission is required
        Toast.makeText(
            requireContext(),
            "Location permission is required to fetch weather data.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.weatherState.collect { weatherState ->
                    Log.d("WeatherActivity", "observeUiState: $weatherState")
                    when (weatherState) {
                        is WeatherUiState.Loading -> {}

                        is WeatherUiState.Idle -> {}

                        is WeatherUiState.Success -> {
                            weatherState.weatherData.let {
                                binding.btnNext7days.isEnabled = true
                                weatherDataList = it
                                binding.tvCityName.text = it.cityName?.split("/")?.get(1) ?: ""
                                binding.tvCurrentTemperature.text =
                                    getString(R.string.current_temperature, it.currentTemperature)

                                updateLineChart(it.hourlyTemperature)

                                binding.tvPressure.text = getString(R.string.pressure_hpa, it.pressure)
                                binding.tvWeatherCondition.text = it.weatherCondition
                                binding.tvMinTemp.text = getString(R.string.min_temp, it.minTemperature)
                                binding.tvMaxTemp.text = getString(R.string.max_temp, it.maxTemperature)
                                binding.tvWindSpeed.text = getString(R.string.wind_m_s, it.windSpeed)
                                binding.tvHumidity.text = getString(R.string.humidity, it.humidity)

                                // Set up daily forecast RecyclerView
                                binding.rvHourlyForecast.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                binding.rvHourlyForecast.adapter = WeatherHourlyAdapter(it.hourlyTemperature, requireContext())

//                                val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL)

//                                binding.rvHourlyForecast.addItemDecoration(divider)
                            }
                        }

                        is WeatherUiState.Error -> {}
                    }
                }
            }
        }
    }

    private suspend fun fetchWeatherForCurrentLocation() {
        // Get current location (latitude and longitude)
        val location = locationHelper.getCurrentLocation()

        Log.d("WeatherActivity", "fetchWeatherForCurrentLocation: $location")

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

//    private fun setWeatherIcon(iconId: String?) {
//        val iconUrl = "${Constants.ICON_URL}$iconId@2x.png"
//
//        Glide.with(this)
//            .load(iconUrl) // Load the icon URL
//            .error(R.drawable.ic_clear_sky)
//            .into(binding.ivWeatherIcon) // Set it into ImageView
//    }
}