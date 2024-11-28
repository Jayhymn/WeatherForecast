package com.wakeupdev.weatherforecast.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wakeupdev.weatherforecast.databinding.ItemCitySearchBinding
import com.wakeupdev.weatherforecast.data.api.City

class CitySearchAdapter(private val cities: List<City>, private val onItemClick: (City) -> Unit) :
    RecyclerView.Adapter<CitySearchAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemCitySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.bind(city)
    }

    override fun getItemCount() = cities.size

    inner class CityViewHolder(private val binding: ItemCitySearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(city: City) {
            binding.tvCityName.text = city.name

            binding.root.setOnClickListener {
                onItemClick(city)
            }
        }
    }
}

