package com.examples.weather.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.examples.weather.R
import com.examples.weather.databinding.ActivityMainBinding
import com.examples.weather.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Menu
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import android.content.Intent
import android.view.View
import com.google.android.libraries.places.widget.AutocompleteActivity.RESULT_ERROR
import com.google.android.libraries.places.api.Places
import com.examples.weather.BuildConfig
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.material.snackbar.Snackbar


const val REQUEST_COARSE_LOCATION = 100
const val REQUEST_PLACES_AUTOCOMPLETE = 200
const val ERROR_MESSAGE_LOCATION = "Failed to retrieve location"
const val ERROR_MESSAGE_FORECAST = "Failed to retrieve forecast"
const val ERROR_MESSAGE_PLACE = "Location search failed"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private val forecastAdapter = ForecastAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        addObservers()
        requestPermissions()

        // Set up the Google Places API client to enable use of autocomplete widget
        Places.initialize(applicationContext, BuildConfig.GooglePlacesApiKey)
        Places.createClient(this)

        // Set up the action bar and recycler view after the view has been inflated and the toolbar instantiated
        setupRecycler()
        setSupportActionBar(toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun addObservers() {

        // Forecast results for recycler view population
        viewModel.openWeatherResponseLiveData.observe(this, Observer { openWeatherModel ->
            forecastAdapter.setForecast(openWeatherModel.forecastSummaries)
            binding.location = openWeatherModel.location.name
        })

        // Location search result status for error message display
        viewModel.locationFailed.observe(this, Observer { locationFailed ->
            locationFailed?.let { displayMessage(constraintMain, ERROR_MESSAGE_LOCATION, Snackbar.LENGTH_LONG) }
        })

        // Forecast query result status for error message display
        viewModel.forecastFailed.observe(this, Observer { forecastFailed ->
            forecastFailed?.let { displayMessage(constraintMain, ERROR_MESSAGE_FORECAST, Snackbar.LENGTH_LONG) }
        })
    }

    private fun displayMessage(view: View, message: String, duration: Int) {
        val snackBar = Snackbar.make(view, message, duration)
        snackBar.show()
    }

    private fun setupRecycler() {
        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_horizontal_trans)!!)
        recyclerForecast.addItemDecoration(itemDecorator)

        recyclerForecast.adapter = forecastAdapter
        recyclerForecast.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_COARSE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_COARSE_LOCATION
            && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            viewModel.getForecastForCurrentLocation()
        } else {
            displayMessage(constraintMain, ERROR_MESSAGE_LOCATION, Snackbar.LENGTH_LONG)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> { displayPlacesAutoComplete(); true }
        R.id.action_location -> { viewModel.getForecastForCurrentLocation(); true }
        else -> { super.onOptionsItemSelected(item) }
    }

    private fun displayPlacesAutoComplete() {
        val fields = listOf(Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypeFilter(TypeFilter.CITIES)
            .setCountry("uk")
            .build(this)
        startActivityForResult(intent, REQUEST_PLACES_AUTOCOMPLETE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PLACES_AUTOCOMPLETE) {
            when (resultCode) {
                RESULT_OK -> {
                    val coordinates = Autocomplete.getPlaceFromIntent(data!!).latLng
                    viewModel.getForecastForSpecifiedLocation(coordinates!!.latitude, coordinates.longitude)
                }
                RESULT_ERROR -> {
                    displayMessage(constraintMain, ERROR_MESSAGE_PLACE, Snackbar.LENGTH_LONG)
                }
                RESULT_CANCELED -> {
                    // Do nothing, place autocomplete widget simply closes
                }
            }
        }
    }
}
