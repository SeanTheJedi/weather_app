package com.rg.weather_richmond.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rg.weather_richmond.R
import com.rg.weather_richmond.models.SavedWeatherData

class HistoryAdapter (private val weatherList:MutableList<SavedWeatherData>)
    : RecyclerView.Adapter<HistoryAdapter.WeatherViewHolder>()
{
    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_history_layout, parent, false)
        return WeatherViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

        val currWeatherData:SavedWeatherData = weatherList.get(position)

        val tvCityName = holder.itemView.findViewById<TextView>(R.id.tv_city_name)
        tvCityName.text = currWeatherData.city

        val details = "Current Temperature: ${currWeatherData.temp}°C" +
                "\nHumidity: ${currWeatherData.humidity}" +
                "\nWeather Condition: ${currWeatherData.conditions}" +
                "\nTime: ${currWeatherData.datetime}"

        val tvDetail = holder.itemView.findViewById<TextView>(R.id.tv_weather_details)
        tvDetail.text = details

    }
}