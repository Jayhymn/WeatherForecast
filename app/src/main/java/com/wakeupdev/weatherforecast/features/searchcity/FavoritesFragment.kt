package com.wakeupdev.weatherforecast.features.searchcity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.FragmentFavoritesBinding
import com.wakeupdev.weatherforecast.databinding.FragmentSearchBinding
import com.wakeupdev.weatherforecast.features.city.data.City
import com.wakeupdev.weatherforecast.features.city.presentation.CityUiState
import com.wakeupdev.weatherforecast.features.city.presentation.CityViewModel
import com.wakeupdev.weatherforecast.shared.Constants.LOCATION_PERMISSION_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites){
    private lateinit var binding: FragmentFavoritesBinding
    private val searchViewModel: CityViewModel by viewModels()
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUiState()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewModel.searchCity(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun observeUiState(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.citiesData.collect { uiState ->
                    when(uiState){
                        is CityUiState.Loading -> {}

                        is CityUiState.Idle -> {}

                        is CityUiState.Success ->{
                            showPopupMenu(uiState.citiesData)
                        }

                        is CityUiState.Error -> {}
                    }
                }
            }
        }

    }

    private fun showPopupMenu(cities: List<City>) {
        Log.d("SearchFragment", "showPopupMenu: $cities")
        // Create a PopupMenu when user taps on the search view or any other UI element
        val menuView = binding.searchView
        val popupMenu = PopupMenu(requireContext(), menuView)

        // Inflate the PopupMenu items dynamically from cities list
        val menu = popupMenu.menu
        cities.forEachIndexed { index, city ->
            menu.add(0, index, index, "${city.name}, ${city.state}, ${city.country}")
        }

        // Set a listener to handle the item selection
        popupMenu.setOnMenuItemClickListener { item ->
            val selectedCity = cities[item.itemId]

            searchViewModel.clearCitiesData()
            Toast.makeText(requireContext(), "Selected: ${selectedCity.name}", Toast.LENGTH_SHORT).show()
            true
        }

        // Show the PopupMenu
        popupMenu.show()
    }
}
