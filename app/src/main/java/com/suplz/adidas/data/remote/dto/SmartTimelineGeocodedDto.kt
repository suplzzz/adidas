package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SmartTimelineGeocodedDto(
    val today: List<GeocodedEventDto>,
    val tomorrow: List<GeocodedEventDto>
)