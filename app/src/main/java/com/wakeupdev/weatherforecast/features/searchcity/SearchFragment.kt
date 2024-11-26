package com.wakeupdev.weatherforecast.features.searchcity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.FragmentSearchBinding
import com.wakeupdev.weatherforecast.features.city.presentation.CityViewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: CityViewModel by viewModels()

    private val cityAdapter = CitySearchAdapter(emptyList()) { city ->
        // Handle city selection (e.g., navigate to weather details or add to favorites)
        Toast.makeText(requireContext(), "${city.name} added to favorites!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.rvCitySearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitySearchResults.adapter = cityAdapter


        return binding.root
    }
}
