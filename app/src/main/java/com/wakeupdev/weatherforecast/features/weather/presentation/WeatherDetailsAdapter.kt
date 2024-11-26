package com.wakeupdev.weatherforecast.features.weather.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.ItemDailyForecastBinding
import com.wakeupdev.weatherforecast.features.weather.data.DailyForecast
import com.wakeupdev.weatherforecast.shared.Constants

class WeatherDetailsAdapter(
    private val dailyForecastList: List<DailyForecast>,
    private val context: Context) :
    RecyclerView.Adapter<WeatherDetailsAdapter.DailyForecastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val binding = ItemDailyForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val dailyForecast = dailyForecastList[position]
        holder.bind(dailyForecast)
    }

    override fun getItemCount(): Int = dailyForecastList.size

    inner class DailyForecastViewHolder(private val binding: ItemDailyForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dailyForecast: DailyForecast) {
            binding.tvDate.text = dailyForecast.date // Format it as needed
            binding.tvMinMaxTemp.text = context.getString(
                R.string.min_to_max_temp,
                dailyForecast.minTemperature,
                dailyForecast.maxTemperature
            )
            // Bind weather icon and other data as required
            // You can use Glide or Coil to load the weather icon image
        }

        private fun setWeatherIcon(iconId: String) {
            val iconUrl = "${Constants.ICON_URL}$iconId@2x.png"

            Glide.with(context)
                .load(iconUrl) // Load the icon URL
                .error(R.drawable.ic_clear_sky)
                .into(binding.ivForecastIcon) // Set it into ImageView
        }
    }
}
