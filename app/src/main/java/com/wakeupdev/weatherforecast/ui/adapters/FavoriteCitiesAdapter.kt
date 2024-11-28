package com.wakeupdev.weatherforecast.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.databinding.ItemFavoriteCityBinding


class FavoriteCitiesAdapter(
    private var cities: List<City>,
    private val listener: FavoriteCityListener? = null
) : RecyclerView.Adapter<FavoriteCitiesAdapter.FavoriteCityHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteCityHolder {
        val binding = ItemFavoriteCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteCityHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteCityHolder, position: Int) {
        holder.bind(cities[position])
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

    interface FavoriteCityListener {
        fun onFavoriteCityItemClick(city: City)
    }

    override fun getItemCount() = cities.size

    inner class FavoriteCityHolder(private val binding: ItemFavoriteCityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: City) {
            binding.tvCityName.text = city.name
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onFavoriteCityItemClick(cities[position])
                }
            }
//            binding.root.contentDescription = "City name: ${city.name}"
        }
    }
}
