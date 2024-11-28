package com.wakeupdev.weatherforecast.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.databinding.FragmentSearchBinding
import com.wakeupdev.weatherforecast.Constants.LOCATION_PERMISSION_REQUEST_CODE
import kotlinx.coroutines.launch
import android.widget.PopupMenu
import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.ui.CityUiState
import com.wakeupdev.weatherforecast.ui.viewmodels.CityViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), OnMapReadyCallback {
    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: CityViewModel by viewModels()
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize the MapView
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Request location permissions if not granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

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

    fun observeUiState(){
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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        } else {
            Toast.makeText(
                requireContext(),
                "Location permission is required to show your current location.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun enableUserLocation() {
        googleMap?.isMyLocationEnabled = true

        // Fetch and show user's current location
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                googleMap?.apply {
                    moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                    addMarker(MarkerOptions().position(currentLocation).title("You are here"))
                }
            } else {
                Toast.makeText(requireContext(), "Unable to fetch current location.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error fetching location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission denied. Unable to show current location.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}