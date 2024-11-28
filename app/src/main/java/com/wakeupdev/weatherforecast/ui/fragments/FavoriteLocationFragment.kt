package com.wakeupdev.weatherforecast.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wakeupdev.weatherforecast.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.databinding.FragmentFavoriteLocationBinding
import com.wakeupdev.weatherforecast.ui.CityUiState
import com.wakeupdev.weatherforecast.ui.adapters.CitySearchAdapter
import com.wakeupdev.weatherforecast.ui.adapters.FavoriteLocationAdapter
import com.wakeupdev.weatherforecast.ui.viewmodels.CityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteLocationFragment : Fragment(R.layout.fragment_favorite_location),
    GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback,
    FavoriteLocationAdapter.CityItemListener, CitySearchAdapter.SearchResultListener {

    private val cityViewModel: CityViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private lateinit var searchBinding: FragmentFavoriteLocationBinding
    private lateinit var favoriteLocationAdapter: FavoriteLocationAdapter
    private lateinit var citySearchAdapter: CitySearchAdapter
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBinding = FragmentFavoriteLocationBinding.bind(view)

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize SupportMapFragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupSearchFragment()

        showFavLocations()

        setUpBottomSheets()

        observeUiState()

        (requireActivity() as AppCompatActivity).setSupportActionBar(searchBinding.toolbar)

        // Enable the navigation icon (back button)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Favorite Locations"
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        setUpMenu()
    }

    private fun setUpMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)

                val searchMenuItem = menu.findItem(R.id.search)
                searchView = searchMenuItem.actionView as SearchView

                searchView.queryHint = "Search locations"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { cityViewModel.searchCity(it) }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        // Optional: Handle text changes
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == android.R.id.home) {
                    handleBackButton()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSearchFragment(){
        val rvSearchResults = searchBinding.recyclerSearchResults
        citySearchAdapter = CitySearchAdapter(emptyList(), this)
        rvSearchResults.adapter = citySearchAdapter
        rvSearchResults.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun handleBackButton() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
            cityViewModel.clearSearchCitiesData()
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showFavLocations() {
        val rvFavLocations = searchBinding.favLocation.rvFavLocs
        favoriteLocationAdapter = FavoriteLocationAdapter(emptyList(), this)
        rvFavLocations.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvFavLocations.adapter = favoriteLocationAdapter
    }

    private fun setUpBottomSheets() {
        // Initialize bottom sheets
        val bottomSheetBehavior = BottomSheetBehavior.from<LinearLayout>(searchBinding.favLocation.bottomSheet)

        // Handle state changes in the bottom sheet
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        searchBinding.favLocation.bottomSheetContainer.setBackgroundColor(
                            resources.getColor(R.color.color_default_background_grey, null)
                        )
                        searchBinding.favLocation.totalOutlets.visibility = View.GONE
                        searchBinding.favLocation.totalOutletsSummary.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        searchBinding.favLocation.bottomSheetIndicatorView.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        searchBinding.favLocation.totalOutlets.visibility = View.VISIBLE
                        searchBinding.favLocation.totalOutletsSummary.visibility = View.GONE
                        searchBinding.favLocation.bottomSheetIndicatorView.visibility = View.VISIBLE
                        searchBinding.favLocation.bottomSheetContainer.setBackgroundColor(0)
                    }

                    else -> Unit
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle slide behavior if needed
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.setOnMarkerClickListener(this)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun enableUserLocation() {
        googleMap?.isMyLocationEnabled = true

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                googleMap?.apply {
                    moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                }
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Toast.makeText(requireContext(), "Marker clicked: ${marker.title}", Toast.LENGTH_SHORT).show()
        return true
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    cityViewModel.citiesSearch,
                    cityViewModel.favCities
                ) { citiesSearchState, favCitiesState ->
                    citiesSearchState to favCitiesState
                }.collect { (citiesSearchState, favCitiesState) ->
                    when (citiesSearchState) {
                        is CityUiState.Success -> citySearchAdapter.updateCities(citiesSearchState.citiesData)
                        else -> Unit // Handle other states if needed
                    }

                    when (favCitiesState) {
                        is CityUiState.Success -> favoriteLocationAdapter.updateCities(favCitiesState.citiesData)
                        else -> Unit // Handle other states if needed
                    }
                }
            }
        }
    }

    override fun onItemClick(city: City) {
        // Handle item click
    }

    override fun onSearchItemClicked(city: City) {

    }
}
