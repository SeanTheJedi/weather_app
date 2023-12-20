package com.rg.weather_richmond.room_interface

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rg.weather_richmond.models.SavedWeatherData

@Dao
interface WeatherDAO {

    @Insert
    fun insertWeatherData(weatherData: SavedWeatherData)

    @Update
    fun updateWeatherData(weatherData: SavedWeatherData)

    @Delete
    fun deleteWeatherData(weatherData: SavedWeatherData)

    @Query("DELETE FROM table_weather")
    fun deleteAllWeatherData()

    @Query("SELECT * FROM table_weather ORDER BY city")
    fun getAllWeatherData(): LiveData<List<SavedWeatherData>>

}