package com.suplz.adidas.data.mapper

import com.suplz.adidas.data.remote.dto.EventRequestDto
import com.suplz.adidas.data.remote.dto.GeocodedEventDto
import com.suplz.adidas.data.remote.dto.SmartTimelineGeocodedDto
import com.suplz.adidas.data.remote.dto.WeatherInfoDto
import com.suplz.adidas.domain.entity.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// --- Мапперы для ответа от вашего бэкенда ---

fun SmartTimelineGeocodedDto.toDomain(): SmartTimelineGeocoded {
    return SmartTimelineGeocoded(
        today = this.today.map { it.toDomain() },
        tomorrow = this.tomorrow.map { it.toDomain() }
    )
}


// в data/mapper/Mappers.kt

// --- Мапперы для ответа от вашего бэкенда ---

// ... SmartTimelineGeocodedDto.toDomain() без изменений ...

@OptIn(ExperimentalTime::class)
fun GeocodedEventDto.toDomain(): GeocodedEvent {
    return GeocodedEvent(
        name = this.name,
        // Преобразуем объект CoordinatesDto в строку "lon,lat"
        userLocationCoords = "${this.userLocationCoords.lon},${this.userLocationCoords.lat}",
        eventLocationCoords = "${this.eventLocationCoords.lon},${this.eventLocationCoords.lat}",
        startTime = Instant.parse(this.startTime),
        endTime = Instant.parse(this.endTime),
        weather = this.weather.toDomain() // Вызываем маппер для погоды
    )
}

fun WeatherInfoDto.toDomain(): WeatherInfo {
    return WeatherInfo(
        // Преобразуем числовые типы в строки, как ожидает доменная модель
        temperature = this.temp.toString(),
        feelsLike = this.feelsLike.toString(),
        condition = this.condition,
        windSpeed = this.windSpeed.toString(),
        windDirection = this.windDir
    )
}
// --- Маппер для запроса на ваш бэкенд ---

@OptIn(ExperimentalTime::class)
fun CalendarEvent.toRequestDto(): EventRequestDto {
    return EventRequestDto(
        name = this.title,
        location = this.locationText ?: "Location not specified",
        startEvent = this.startTime.toString(),
        endEvent = this.endTime.toString()
    )
}