package com.suplz.adidas.domain.entity

data class TimelineItem(
    val name: String,
    val userLocation: String,
    val eventLocation: String,
    val travelDuration: String,
    val transportOptions: List<TransportOption>,
    val weather: WeatherInfo,
)