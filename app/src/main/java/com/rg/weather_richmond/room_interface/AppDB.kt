package com.rg.weather_richmond.room_interface

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rg.weather_richmond.models.SavedWeatherData
import java.util.concurrent.Executors

@Database(entities = [SavedWeatherData::class], version = 1, exportSchema = false)
abstract class AppDB: RoomDatabase() {

    abstract fun weatherDAO(): WeatherDAO

    companion object{

        var db:AppDB? = null
        private  const val NUMBER_OF_THREADS = 4
        val databaseQueryExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDB(context: Context) : AppDB? {
            if (db == null) {
                db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "com.rg.weather_richmond.room_interface"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return db
        }

    }
}