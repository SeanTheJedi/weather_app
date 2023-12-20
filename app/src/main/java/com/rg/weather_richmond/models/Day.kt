package com.rg.weather_richmond.models

data class Day(
    val datetime: String,
    val temp: Double,
    val humidity: Double,
    val conditions: String,
)
