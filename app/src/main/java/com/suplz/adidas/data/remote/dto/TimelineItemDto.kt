package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimelineItemDto(
    val name: String,
    @SerialName("user_location") val userLocation: String,
    @SerialName("event_location") val eventLocation: String,
    @SerialName("toEvent_duration") val travelDuration: String,
    @SerialName("transport_types") val transportOptions: List<TransportOptionDto>,
    val weather: WeatherInfoDto
)