package com.rg.weather_richmond.models

data class WeatherBaseClass(
    val queryCost: Long,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val address: String,
    val timezone: String,
    val tzoffset: Double,
    val days: List<Day>,
    val currentConditions: CurrentConditions,
)
