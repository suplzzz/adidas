package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class WeatherInfoDto(
    val temp: Int, // <-- Тип изменен на Int
    @SerialName("feels_like") val feelsLike: Int, // <-- Тип изменен на Int
    val condition: String,
    @SerialName("wind_speed") val windSpeed: Int, // <-- Тип изменен на Int
    @SerialName("wind_dir") val windDir: String
)