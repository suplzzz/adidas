package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Этот DTO описывает один объект события с координатами от вашего бэка
@Serializable
data class GeocodedEventDto(
    val name: String,
    // Убедитесь, что бэкенд присылает именно эти имена полей в JSON
    @SerialName("user_location") val userLocationCoords: CoordinatesDto,
    @SerialName("event_location") val eventLocationCoords: CoordinatesDto,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String, // <-- ДОБАВЛЕНО ЭТО ПОЛЕ
    val weather: WeatherInfoDto
)