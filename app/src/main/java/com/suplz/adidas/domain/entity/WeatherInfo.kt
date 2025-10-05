package com.suplz.adidas.domain.entity


data class WeatherInfo(
    val temperature: String,
    val feelsLike: String,
    val condition: String,
    val windSpeed: String,
    val windDirection: String
)