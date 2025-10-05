package com.suplz.adidas.domain.repository

import com.suplz.adidas.domain.entity.TransportType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Интерфейс в domain слое
interface DirectionsRepository {
    // Возвращает длительность маршрута в секундах
    @OptIn(ExperimentalTime::class)
    suspend fun getRouteDurationSeconds(
        fromCoords: String,
        toCoords: String,
        type: TransportType,
        departureTime: Instant // Принимаем удобный для логики тип Instant
    ): Int?
}