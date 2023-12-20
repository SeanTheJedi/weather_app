package com.rg.weather_richmond.room_interface

import android.app.Application
import androidx.lifecycle.LiveData
import com.rg.weather_richmond.models.SavedWeatherData

class WeatherRepository(application: Application) {

    private var db: AppDB? = null
    private val weatherDAO = AppDB.getDB(application)?.weatherDAO()

    var allWeatherData : LiveData<List<SavedWeatherData>>? = weatherDAO?.getAllWeatherData()

    init {
        this.db = AppDB.getDB(application)
    }

    fun insertWeatherData(datToInsert: SavedWeatherData) {
        AppDB.databaseQueryExecutor.execute{
            this.weatherDAO?.insertWeatherData(datToInsert)
        }
    }

    fun updateWeatherData(dataToUpdate: SavedWeatherData) {
        AppDB.databaseQueryExecutor.execute{
            this.weatherDAO?.updateWeatherData(dataToUpdate)
        }
    }

    fun deleteWeatherData(dataToDelete: SavedWeatherData) {
        AppDB.databaseQueryExecutor.execute{
            this.weatherDAO?.deleteWeatherData(dataToDelete)
        }
    }

    fun deleteAllWeatherData() {
        AppDB.databaseQueryExecutor.execute{
            this.weatherDAO?.deleteAllWeatherData()
        }
    }

}