package com.wakeupdev.weatherforecast.features.weather.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.DailyForecastItemBinding
import com.wakeupdev.weatherforecast.features.weather.data.DailyForecast
import com.wakeupdev.weatherforecast.shared.Constants

class WeatherDailyAdapter(
    private val dailyForecastList: List<DailyForecast>,
    private val context: Context) :
    RecyclerView.Adapter<WeatherDailyAdapter.DailyForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding = DailyForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val dailyForecast = dailyForecastList[position]
        holder.bind(dailyForecast)
    }

    override fun getItemCount(): Int = dailyForecastList.size

    inner class DailyForecastViewHolder(private val binding: DailyForecastItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dailyForecast: DailyForecast) {
            val dayDate = dailyForecast.date?.split(" ")

            binding.tvDay.text = dayDate?.get(0) ?: ""
            binding.tvDate.text = dayDate?.subList(1, dayDate.size)?.joinToString(" ") ?: ""
            binding.tvMinTemp.text = context.getString(R.string.min_temp, dailyForecast.minTemperature)
            binding.tvMaxTemp.text = context.getString(R.string.max_temp, dailyForecast.maxTemperature)

            setWeatherIcon(dailyForecast.weatherIcon)
        }

        private fun setWeatherIcon(iconId: String?) {
            val iconUrl = "${Constants.ICON_URL}$iconId@2x.png"

            Glide.with(context)
                .load(iconUrl) // Load the icon URL
                .error(R.drawable.ic_clear_sky)
                .into(binding.ivForecastIcon) // Set it into ImageView
        }
    }
}
