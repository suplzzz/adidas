package com.suplz.adidas.data.repository

import com.suplz.adidas.data.remote.api.GisGlobalRoutingApi
import com.suplz.adidas.data.remote.api.GisPublicTransportApi
import com.suplz.adidas.data.remote.dto.gis.GisGlobalRouteRequestDto
import com.suplz.adidas.data.remote.dto.gis.GisPublicTransportRequestDto
import com.suplz.adidas.data.remote.dto.gis.PointDto
import com.suplz.adidas.domain.entity.TransportType
import com.suplz.adidas.domain.repository.DirectionsRepository
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Реализация в data слое
@OptIn(ExperimentalTime::class)
class DirectionsRepositoryImpl @Inject constructor(
    private val globalRoutingApi: GisGlobalRoutingApi,
    private val publicTransportApi: GisPublicTransportApi
) : DirectionsRepository {

    override suspend fun getRouteDurationSeconds(
        fromCoords: String,
        toCoords: String,
        type: TransportType,
        departureTime: Instant
    ): Int? {
        return try {
            val (fromLon, fromLat) = parseCoords(fromCoords)
            val (toLon, toLat) = parseCoords(toCoords)

            // Конвертируем Instant в Unix-время ОДИН РАЗ для всех API
            val departureTimeUnix = departureTime.epochSeconds

            when (type) {
                TransportType.CAR, TransportType.FOOT, TransportType.TAXI, TransportType.SCOOTER -> {
                    val points = listOf(PointDto(x = fromLon, y = fromLat), PointDto(x = toLon, y = toLat))
                    val request = GisGlobalRouteRequestDto(
                        points = points,
                        type = getGlobalRoutingApiType(type),
                        utc = departureTimeUnix
                    )
                    globalRoutingApi.getRoute(request).result?.routes?.firstOrNull()?.duration
                }
                TransportType.BUS -> {
                    val sourcePoint = PointDto(x = fromLon, y = fromLat)
                    val targetPoint = PointDto(x = toLon, y = toLat)
                    val request = GisPublicTransportRequestDto(
                        source = sourcePoint,
                        target = targetPoint,
                        startTime = departureTimeUnix
                    )
                    publicTransportApi.getRoute(request).result?.items?.firstOrNull()?.totalDuration
                }
                TransportType.UNKNOWN -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getGlobalRoutingApiType(type: TransportType): String = when(type) {
        TransportType.CAR -> "jam"
        TransportType.FOOT -> "pedestrian"
        TransportType.SCOOTER -> "pedestrian" // API не имеет отдельного типа для самоката, используем пешехода
        TransportType.TAXI -> "taxi"
        else -> "jam"
    }

    private fun parseCoords(coords: String): Pair<Double, Double> {
        return try {
            val (lon, lat) = coords.split(',').map { it.trim().toDouble() }
            lon to lat
        } catch (e: Exception) {
            // Возвращаем дефолтные координаты или бросаем ошибку, чтобы избежать падения
            0.0 to 0.0
        }
    }
}