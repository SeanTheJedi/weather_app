package com.rg.weather_richmond.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_weather")
class SavedWeatherData (
    val city: String,
    val datetime: String,
    val temp: Double,
    val humidity: Double,
    val conditions: String,
){
    @PrimaryKey(autoGenerate = true)
    var id = 0

    override fun toString(): String {
        return "SavedWeatherData(city='$city', datetime='$datetime', temp=$temp, humidity=$humidity, conditions='$conditions', id=$id)"
    }
}