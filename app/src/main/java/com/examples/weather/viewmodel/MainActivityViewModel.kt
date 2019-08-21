package com.examples.weather.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.examples.weather.BuildConfig
import com.examples.weather.model.OpenWeatherModel
import com.examples.weather.model.OpenWeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainActivityViewModel(application: Application) : AndroidViewModel(application), OnSuccessListener<Location>, OnFailureListener {

    private val appContext = getApplication() as Context
    private val forecastService by lazy { OpenWeatherService.create() }
    private var userLocation = Location("default")

    // Observable data for the view - forecast results and status values
    val openWeatherResponseLiveData = MutableLiveData<OpenWeatherModel.Forecast>()
    var forecastFailed = MutableLiveData<Boolean?>()
    var locationFailed = MutableLiveData<Boolean?>()

    fun getForecastForCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        if (ContextCompat.checkSelfPermission(appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this).addOnFailureListener(this)
        }
        else {
            locationFailed.value = true
        }
    }

    fun getForecastForSpecifiedLocation(latitude: Double, longitude: Double) {

        val serviceCall: Call<OpenWeatherModel.Forecast> = forecastService.forecast(
            BuildConfig.OpenWeatherApiKey,
            latitude,
            longitude,
            OpenWeatherService.UNITS_METRIC)

        serviceCall.enqueue(object: Callback<OpenWeatherModel.Forecast> {
            override fun onResponse(call: Call<OpenWeatherModel.Forecast>, response: Response<OpenWeatherModel.Forecast>) {
                openWeatherResponseLiveData.value = response.body()
            }

            override fun onFailure(call: Call<OpenWeatherModel.Forecast>, throwable: Throwable) {
                Log.d("FORECAST ERROR", throwable.message)
                forecastFailed.value = true
            }
        }
        )
    }

    override fun onSuccess(location: Location?) {

        if (location == null) {
            Log.d("LOCATION ERROR", "Location not found")
            locationFailed.value = true
        }
        else {
            userLocation.latitude = location.latitude
            userLocation.longitude = location.longitude

            getForecastForSpecifiedLocation(userLocation.latitude, userLocation.longitude)
        }
    }

    override fun onFailure(exception: Exception) {
        Log.d("LOCATION ERROR", exception.message)
        locationFailed.value = true
    }
}