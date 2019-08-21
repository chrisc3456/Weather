package com.examples.weather.model

import com.examples.weather.BuildConfig
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenWeatherService {

    companion object {

        const val UNITS_DEFAULT = "standard"
        const val UNITS_METRIC = "metric"
        const val UNITS_IMPERIAL = "imperial"

        fun create(): OpenWeatherService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.OpenWeatherBaseUrl)
                .build()

            return retrofit.create(OpenWeatherService::class.java)
        }
    }

    @GET("forecast")
    fun forecast(
        @Query("appid") apiKey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String
    ): Call<OpenWeatherModel.Forecast>
}