package com.rg.weather_richmond

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rg.weather_richmond.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.rg.weather_richmond.api_interface.Requests
import com.rg.weather_richmond.api_interface.RetrofitInstance
import com.rg.weather_richmond.models.SavedWeatherData
import com.rg.weather_richmond.models.WeatherBaseClass
import com.rg.weather_richmond.room_interface.WeatherRepository
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val TAG: String = this.toString()
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var currentLocation: LatLng = LatLng(43.74853, -79.26374)
    private var currentWeatherData:SavedWeatherData? = null
    private lateinit var weatherRepository: WeatherRepository

    private val APP_PERMISSIONS_LIST = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val multiplePermissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultsList ->
        Log.d(TAG, resultsList.toString())

        var allPermissionsGrantedTracker = true
        for (item in resultsList.entries) {
            if (item.key in APP_PERMISSIONS_LIST && item.value == false) {
                allPermissionsGrantedTracker = false
            }
        }

        if (allPermissionsGrantedTracker == true) {
            var snackbar =
                Snackbar.make(binding.root, "All permissions granted", Snackbar.LENGTH_LONG)
            snackbar.show()
            getDeviceLocation()

        } else {
            var snackbar =
                Snackbar.make(binding.root, "Some permissions NOT granted", Snackbar.LENGTH_LONG)
            snackbar.show()

        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        weatherRepository = WeatherRepository(application)

        multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)

        binding.btnGetWeatherReport.setOnClickListener {
            getWeatherReport()
        }

        binding.btnSaveWeatherReport.setOnClickListener {
            saveWeatherData()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_show_history -> {
                var intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location === null) {
                    Log.d(TAG, "Location is null")
                    return@addOnSuccessListener
                }
                // Output the location
                val message =
                    "The device is located at: ${location.latitude}, ${location.longitude}"
                Log.d(TAG, message)
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()

                getStreetAddress(location.latitude, location.longitude)



            }
    }


    fun getStreetAddress(lat:Double, lng: Double) {
        val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())

        Log.d(TAG, "Getting address for ${lat}, ${lng}")

        try {
            val searchResults: MutableList<Address>? =
                geocoder.getFromLocation(lat, lng, 1)

            if (searchResults == null) {
                Log.e(TAG, "getting Street Address: searchResults is NULL ")
                return
            }

            if (searchResults.size == 0) {
                Log.d(TAG, "Search results <= 0")
            } else {

                val matchingAddress: Address = searchResults.get(0)
                val output = "${matchingAddress.locality}, ${matchingAddress.countryName}"
                binding.etCityName.setText(output)

            }

        } catch (ex: Exception) {
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
        }

    }

    fun getCoordinates(cityFromUI: String): Address?{

        val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())

        Log.d(TAG, "Getting coordinates for ${cityFromUI}")

        try {
            val searchResults: MutableList<Address>? =
                geocoder.getFromLocationName(cityFromUI, 1)
            if (searchResults == null) {
                Log.e(TAG, "searchResults variable is null")
                return null
            }

            if (searchResults.size == 0) {
                binding.tvResults.setText("Search results are empty.")
                return null
            } else {
                // Get the coordinate
                val foundLocation: Address = searchResults.get(0)
                // output to screen
                var message =
                    "Coordinates are: ${foundLocation.latitude}, ${foundLocation.longitude}"
                Log.d(TAG, message)

                return foundLocation
            }


        } catch (ex: Exception) {
            Log.e(TAG, "Error encountered while getting coordinate location.")
            Log.e(TAG, ex.toString())
            return null
        }

    }

    fun getWeatherReport(){
        var snackbar =
            Snackbar.make(binding.root, "Getting Weather data", Snackbar.LENGTH_LONG)
        snackbar.show()

        val cityFromUI = binding.etCityName.text.toString()

        val coordinates = getCoordinates(cityFromUI)

        if(coordinates != null) {
            var api:Requests = RetrofitInstance.retrofitService

            lifecycleScope.launch {
                val weatherData: WeatherBaseClass = api.getWeatherData(coordinates.latitude, coordinates.longitude)

                val output = "Current Temperature: ${weatherData.currentConditions.temp}°C" +
                             "\nHumidity: ${weatherData.currentConditions.humidity}" +
                             "\nWeather Condition: ${weatherData.currentConditions.conditions}" +
                             "\nTime: ${weatherData.currentConditions.datetime}"

                binding.tvResults.setText(output)

                currentWeatherData = SavedWeatherData(
                    cityFromUI,
                    weatherData.currentConditions.datetime,
                    weatherData.currentConditions.temp,
                    weatherData.currentConditions.humidity,
                    weatherData.currentConditions.conditions
                )

                Log.d("WEATHER_DATA", weatherData.toString())
            }
        }
    }

    fun saveWeatherData() {
        var snackbar =
            Snackbar.make(binding.root, "Saving weather data", Snackbar.LENGTH_LONG)
        snackbar.show()
        lifecycleScope.launch {
            Log.d(TAG, "saveData: $currentWeatherData")
            weatherRepository.insertWeatherData(currentWeatherData!!)
             snackbar =
                Snackbar.make(binding.root, "Saved weather data", Snackbar.LENGTH_LONG)
             snackbar.show()
        }
    }
}