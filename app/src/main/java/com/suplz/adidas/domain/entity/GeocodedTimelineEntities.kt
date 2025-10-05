package com.suplz.adidas.domain.entity

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Описывает одно событие, обогащенное координатами
data class GeocodedEvent @OptIn(ExperimentalTime::class) constructor(
    val name: String,
    val userLocationCoords: String,
    val eventLocationCoords: String,
    val startTime: Instant,
    val endTime: Instant, // <-- ДОБАВЛЕНО ЭТО ПОЛЕ
    val weather: WeatherInfo
)

// Описывает ответ от вашего бэкенда: списки событий с координатами
data class SmartTimelineGeocoded(
    val today: List<GeocodedEvent>,
    val tomorrow: List<GeocodedEvent>
)