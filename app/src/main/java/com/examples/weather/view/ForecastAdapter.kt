package com.examples.weather.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.examples.weather.R
import com.examples.weather.model.OpenWeatherModel
import java.text.SimpleDateFormat
import androidx.databinding.DataBindingUtil
import com.examples.weather.databinding.ListItemForecastBinding
import com.examples.weather.databinding.ListItemForecastCurrentBinding
import org.joda.time.DateTimeComparator
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.math.roundToInt

const val VIEW_TYPE_FIRST: Int = 0
const val VIEW_TYPE_LIST: Int = 1

class ForecastAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var forecastSummaries: MutableList<OpenWeatherModel.Summary> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        var viewHolder: RecyclerView.ViewHolder

        /*
            The first list item will have a different layout to the rest so use a different version
            of the binding and layout based on the view type parameter
         */
        if (viewType == VIEW_TYPE_FIRST) {
            val binding: ListItemForecastCurrentBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.list_item_forecast_current, parent, false)
            viewHolder = CurrentViewHolder(binding)
        }
        else {
            val binding: ListItemForecastBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.list_item_forecast, parent, false)
            viewHolder = ListViewHolder(binding)
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return forecastSummaries.size
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = VIEW_TYPE_LIST
        if (position == 0) {
            viewType = VIEW_TYPE_FIRST
        }
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val summary = forecastSummaries[position]

        if (holder is CurrentViewHolder) {
            holder.binding.imageUrl = getWeatherIconUrl(summary.weatherSummaries.first().icon)
            holder.binding.date = getDateAsText(position).capitalize()
            holder.binding.description = summary.weatherSummaries.first().description.capitalize()
            holder.binding.humidity = summary.keyValues.humidity.toString() + "%"
            holder.binding.windSpeed = summary.windDetails.speed.roundToInt().toString() + "MPH"
            holder.binding.temperature = summary.keyValues.temperature.roundToInt().toString() + "\u2103"

            holder.binding.executePendingBindings()
        }
        else if (holder is ListViewHolder) {
            holder.binding.imageUrl = getWeatherIconUrl(summary.weatherSummaries.first().icon)
            holder.binding.date = getDateAsText(position).capitalize()
            holder.binding.description = summary.weatherSummaries.first().description.capitalize()
            holder.binding.temperature = summary.keyValues.temperature.roundToInt().toString() + "\u2103"

            holder.binding.executePendingBindings()
        }
    }

    private fun getDateAsText(position: Int): String {
        val forecastDate = Date(forecastSummaries[position].dateTime * 1000)
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DATE, 1)
        val tomorrow = calendar.time
        val dateTimeComparator: DateTimeComparator = DateTimeComparator.getDateOnlyInstance()

        return when {
            dateTimeComparator.compare(forecastDate, today) == 0 -> "Today, " + SimpleDateFormat("h:mm aa").format(forecastDate)
            dateTimeComparator.compare(forecastDate, tomorrow) == 0 -> "Tomorrow, " + SimpleDateFormat("h:mm aa").format(forecastDate)
            else -> SimpleDateFormat("EEEE, h:mm aa").format(forecastDate)
        }
    }

    private fun getWeatherIconUrl(iconID: String): String {
        return "http://openweathermap.org/img/wn/$iconID@2x.png"
    }

    fun setForecast(forecast: MutableList<OpenWeatherModel.Summary>) {
        this.forecastSummaries = forecast
        notifyDataSetChanged()
    }

    class CurrentViewHolder(val binding: ListItemForecastCurrentBinding) : RecyclerView.ViewHolder(binding.root)
    class ListViewHolder(val binding: ListItemForecastBinding) : RecyclerView.ViewHolder(binding.root)
}