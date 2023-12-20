package com.rg.weather_richmond.models

data class CurrentConditions(
    val datetime: String,
    val temp: Double,
    val humidity: Double,
    val conditions: String,
)
