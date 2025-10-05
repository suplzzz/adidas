package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoordinatesDto(
    val lat: Double,
    val lon: Double
)