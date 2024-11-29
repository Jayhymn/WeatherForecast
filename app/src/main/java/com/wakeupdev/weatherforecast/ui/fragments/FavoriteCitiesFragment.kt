package com.wakeupdev.weatherforecast.ui.fragments

import FavoriteCitiesAdapter
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
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
import com.google.android.material.snackbar.Snackbar
import com.wakeupdev.weatherforecast.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.databinding.FragmentFavoriteCitiesBinding
import com.wakeupdev.weatherforecast.ui.CityUiState
import com.wakeupdev.weatherforecast.ui.adapters.CitySearchAdapter
import com.wakeupdev.weatherforecast.ui.viewmodels.CityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteCitiesFragment : Fragment(R.layout.fragment_favorite_cities),
    GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback,
    FavoriteCitiesAdapter.FavoriteCityListener, CitySearchAdapter.SearchResultListener {

    private val cityViewModel: CityViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private lateinit var searchBinding: FragmentFavoriteCitiesBinding
    private lateinit var favoriteLocationAdapter: FavoriteCitiesAdapter
    private lateinit var citySearchAdapter: CitySearchAdapter
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBinding = FragmentFavoriteCitiesBinding.bind(view)

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

    private var currentMenu: Menu? = null

    private fun setUpMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                currentMenu = menu // Store the reference to the menu

                menuInflater.inflate(R.menu.search_menu, menu)

                val searchMenuItem = menu.findItem(R.id.search)
                searchView = searchMenuItem.actionView as SearchView

                searchView.queryHint = "Search locations"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { cityViewModel.searchCity(it) }
                        hideKeyboard()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        // Optional: Handle text changes
                        if (newText?.isEmpty() == true) {
                            cityViewModel.clearSearchCitiesData()
                        }
                        return true
                    }
                })

                val deleteMenuItem = menu.findItem(R.id.delete)
                deleteMenuItem.setOnMenuItemClickListener {
                    handleDeleteButton()
                    true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        handleBackButton()

                        // Show the delete item when home button is pressed
                        currentMenu?.findItem(R.id.delete)?.isVisible = true

                        return true
                    }
                    R.id.search -> {
                        // Hide the delete item when search button is pressed
                        currentMenu?.findItem(R.id.delete)?.isVisible = false

                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun hideKeyboard() {
        // Get the InputMethodManager system service
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext()) // Get the current focus view or create a new one

        // Hide the soft keyboard
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun setupSearchFragment(){
        val rvSearchResults = searchBinding.recyclerSearchResults
        citySearchAdapter = CitySearchAdapter(emptyList(), this)
        rvSearchResults.adapter = citySearchAdapter
        rvSearchResults.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        rvSearchResults.addItemDecoration(dividerItemDecoration)
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
        favoriteLocationAdapter = FavoriteCitiesAdapter(emptyList(), this)
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
                        searchBinding.favLocation.tvTotalFavCities.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        searchBinding.favLocation.bottomSheetIndicatorView.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        searchBinding.favLocation.totalOutlets.visibility = View.VISIBLE
                        searchBinding.favLocation.tvTotalFavCities.visibility = View.GONE
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
                    moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
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
                        is CityUiState.Success -> {
                            searchBinding.progressBar.visibility = View.GONE
                            if (citiesSearchState.citiesData.isEmpty()){
                                searchBinding.recyclerSearchResults.visibility = View.GONE
                            } else {
                                searchBinding.recyclerSearchResults.visibility = View.VISIBLE
                            }
                            citySearchAdapter.updateCities(citiesSearchState.citiesData)
                        }

                        is CityUiState.Loading -> {
                            searchBinding.progressBar.visibility = View.VISIBLE
                            searchBinding.recyclerSearchResults.visibility = View.VISIBLE
                        }

                        is CityUiState.Error -> {
                            searchBinding.progressBar.visibility = View.GONE
                            Snackbar.make(searchBinding.root, "Please check your connection and retry!", Snackbar.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }

                    when (favCitiesState) {
                        is CityUiState.Success -> {
                            favoriteLocationAdapter.updateCities(favCitiesState.citiesData)
                            searchBinding.favLocation.tvTotalFavCities.text = "Total: ${favCitiesState.citiesData.size}"
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(selectedCities: List<City>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Selected Cities")
        builder.setMessage("Are you sure you want to delete ${selectedCities.size} selected cities?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Delete selected cities
            deleteSelectedCities(selectedCities)
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    private fun handleDeleteButton() {
        val selectedCities = favoriteLocationAdapter.getSelectedCities()

        Log.d("FavoriteCitiesFragment", "handleDeleteButton: $selectedCities")
        if (selectedCities.isEmpty()) {
            // Show Snackbar if no cities are selected
            Snackbar.make(requireView(), "No selections made", Snackbar.LENGTH_SHORT).show()
        } else {
            // Show confirmation dialog if cities are selected
            showDeleteConfirmationDialog(selectedCities)
        }
    }

    private fun deleteSelectedCities(selectedCities: List<City>) {
        cityViewModel.deleteCities(selectedCities)

        // Clear the selection in the adapter
        favoriteLocationAdapter.clearSelection()

        // Show success Snackbar
        Snackbar.make(requireView(), "Successfully deleted selected cities", Snackbar.LENGTH_SHORT).show()
    }

    override fun onFavoriteCityItemClick(city: City) {
        val weatherFragment = WeatherFragment()
        val bundle = Bundle()
        bundle.putDouble("lat", city.latitude)
        bundle.putDouble("lon", city.longitude)
        bundle.putString("city_name", city.name)

        weatherFragment.arguments = bundle

        parentFragmentManager.commit {
            replace(R.id.fragmentContainer, weatherFragment)
            addToBackStack(null)
        }
    }

    override fun onSearchItemClicked(city: City) {
        val cityName = "${city.name}, ${city.state}, ${city.country}"

        val weatherDialog = WeatherDialogFragment.newInstance(city, cityName)
        weatherDialog.show(parentFragmentManager, "WeatherDialog")
    }
}