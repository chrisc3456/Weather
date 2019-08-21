package com.examples.weather.model

import com.google.gson.annotations.SerializedName

object OpenWeatherModel {

    /*
        Data classes to align with the API provided by OpenWeather - https://openweathermap.org/forecast5
        Using GSON serialisations to match directly with JSON field names in the API
     */

    data class Forecast(
        @SerializedName("city") val location: LocationDetails,
        @SerializedName("list") val forecastSummaries: MutableList<Summary>
    )

    data class Summary(
        @SerializedName("dt") val dateTime: Long,
        @SerializedName("main") val keyValues: KeyValues,
        @SerializedName("weather") val weatherSummaries: MutableList<WeatherSummary>,
        @SerializedName("clouds") val cloudsDetails: CloudDetails,
        @SerializedName("wind") val windDetails: WindDetails,
        @SerializedName("rain") val rainDetails: RainDetails,
        @SerializedName("snow") val snowDetails: SnowDetails
    )

    data class KeyValues(
        @SerializedName("temp") val temperature: Double,
        @SerializedName("temp_min") val temperatureMin: Double,
        @SerializedName("temp_max") val temperatureMax: Double,
        @SerializedName("pressure") val pressure: Double,
        @SerializedName("sea_level") val pressureSeaLevel: Double,
        @SerializedName("ground_level") val pressureGroundLevel: Double,
        @SerializedName("humidity") val humidity: Int
    )

    data class WeatherSummary(
        @SerializedName("main") val main: String,
        @SerializedName("description") val description: String,
        @SerializedName("icon") val icon: String
    )

    data class CloudDetails(
        @SerializedName("all") val cloudPercentage: Int
    )

    data class WindDetails(
        @SerializedName("speed") val speed: Double,
        @SerializedName("deg") val direction: Double
    )

    data class RainDetails(
        @SerializedName("3h") val volumeThreeHours: Double
    )

    data class SnowDetails(
        @SerializedName("3h") val volumeThreeHours: Double
    )

    data class LocationDetails(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("coord") val coordinate: Coordinate
    )

    data class Coordinate(
        @SerializedName("latitude") val latitude: Long,
        @SerializedName("longitude") val longitude: Long
    )
}