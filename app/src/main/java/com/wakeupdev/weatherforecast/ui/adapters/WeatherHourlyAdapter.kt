package com.wakeupdev.weatherforecast.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.HourlyItemForecastBinding
import com.wakeupdev.weatherforecast.data.HourlyTemperature
import com.wakeupdev.weatherforecast.Constants

class WeatherHourlyAdapter(
    private val hourlyTemperatures: List<HourlyTemperature>,
    private val context: Context) :
    RecyclerView.Adapter<WeatherHourlyAdapter.HourlyForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val binding = HourlyItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val dailyForecast = hourlyTemperatures[position]
        holder.bind(dailyForecast)
    }

    override fun getItemCount(): Int = hourlyTemperatures.size

    inner class HourlyForecastViewHolder(private val binding: HourlyItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hourlyTemp: HourlyTemperature) {
            binding.tvTime.text = hourlyTemp.date // Format it as needed
            binding.tvTemp.text = context.getString(
                R.string.temp,
                hourlyTemp.temperature,
            )

            setWeatherIcon(hourlyTemp.weatherIcon)
        }

        private fun setWeatherIcon(iconId: String?) {
            val iconUrl = "${Constants.ICON_URL}$iconId@2x.png"

            Glide.with(context)
                .load(iconUrl) // Load the icon URL
                .error(R.drawable.ic_clear_sky)
                .into(binding.ivWeatherIcon) // Set it into ImageView
        }
    }
}
