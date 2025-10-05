package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransportOptionDto(
    val type: String,
    val duration: String,
    @SerialName("status_color") val statusColor: String
)
