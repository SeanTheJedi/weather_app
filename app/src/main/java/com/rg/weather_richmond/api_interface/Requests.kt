package com.rg.weather_richmond.api_interface

import com.rg.weather_richmond.models.WeatherBaseClass
import retrofit2.http.GET
import retrofit2.http.Path

interface Requests {

    @GET("VisualCrossingWebServices/rest/services/timeline/{lat},{lng}/today?unitGroup=metric&elements=datetime,temp,humidity,conditions&include=current&key=M8QU3R6PYK8JY927R7WHGGXBD&contentType=json")
    suspend fun getWeatherData(@Path("lat") lat:Double, @Path("lng") lng:Double): WeatherBaseClass
}