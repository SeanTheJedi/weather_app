package com.rg.weather_richmond

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rg.weather_richmond.adapters.HistoryAdapter
import com.rg.weather_richmond.databinding.ActivityHistoryBinding
import com.rg.weather_richmond.models.SavedWeatherData
import com.rg.weather_richmond.room_interface.WeatherRepository

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var weatherRepository: WeatherRepository
    private val TAG: String = this.toString()
    private var weatherList: MutableList<SavedWeatherData> = mutableListOf()
    private lateinit var weatherAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        weatherRepository = WeatherRepository(application)
        weatherAdapter = HistoryAdapter(weatherList)

        binding.rvItems.adapter = weatherAdapter
        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

    }

    override fun onStart() {
        super.onStart()
        this.weatherRepository.allWeatherData?.observe(this) { receivedWeatherData ->
            if (receivedWeatherData.isNotEmpty()) {
                Log.d(TAG, "onStart: ReceivedNotes: $receivedWeatherData")

                weatherList.clear()
                weatherList.addAll(receivedWeatherData)
                weatherAdapter.notifyDataSetChanged()

            }

        }
    }
}