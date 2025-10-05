package com.suplz.adidas.data.remote.dto.gis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- DTO для Global Routing API (POST) ---

@Serializable
data class GisGlobalRouteRequestDto(
    val points: List<PointDto>,
    val type: String,
    val utc: Long? = null // Время в формате Unix timestamp (секунды)
)

@Serializable
data class PointDto(
    val type: String = "point",
    val x: Double, // Долгота (longitude)
    val y: Double // Широта (latitude)
)

@Serializable
data class GisGlobalRouteResponseDto(val result: GisGlobalRouteResultDto?)
@Serializable
data class GisGlobalRouteResultDto(val routes: List<GisRouteDto>?)
@Serializable
data class GisRouteDto(val duration: Int) // Время в секундах


// --- DTO для Public Transport API (GET) ---

@Serializable
data class GisPublicTransportRequestDto(
    val source: PointDto,
    val target: PointDto,
    @SerialName("start_time") val startTime: Long? = null, // Время в формате Unix timestamp (секунды)
    val transport: List<String> = listOf("bus", "trolleybus", "tram", "minibus", "subway")
)

@Serializable
data class GisPublicTransportResponseDto(val result: GisPublicTransportResultDto?)
@Serializable
data class GisPublicTransportResultDto(val items: List<GisPublicTransportItemDto>?)
@Serializable
data class GisPublicTransportItemDto(@SerialName("total_duration") val totalDuration: Int) // Время в секундах