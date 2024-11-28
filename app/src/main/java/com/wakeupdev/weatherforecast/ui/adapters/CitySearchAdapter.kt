package com.wakeupdev.weatherforecast.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wakeupdev.weatherforecast.databinding.ItemCitySearchBinding
import com.wakeupdev.weatherforecast.data.api.City

class CitySearchAdapter(private var cities: List<City>, private val searchListener: SearchResultListener) :
    RecyclerView.Adapter<CitySearchAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemCitySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.bind(city)
    }

    fun updateCities(newCities: List<City>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = cities.size
            override fun getNewListSize() = newCities.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return cities[oldItemPosition].name == newCities[newItemPosition].name
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return cities[oldItemPosition] == newCities[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        cities = newCities
        diffResult.dispatchUpdatesTo(this)
    }

    interface SearchResultListener {
        fun onSearchItemClicked(city: City)
    }

    override fun getItemCount() = cities.size

    inner class CityViewHolder(private val binding: ItemCitySearchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(city: City) {
            binding.cityName.text = "${city.name}, ${city.state}, ${city.country}"

            binding.root.setOnClickListener {
                searchListener.onSearchItemClicked(city)
            }
        }
    }
}

