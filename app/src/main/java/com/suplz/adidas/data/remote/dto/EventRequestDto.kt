package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRequestDto(
    val name: String,
    val location: String,
    @SerialName("start_event") val startEvent: String,
    @SerialName("end_event") val endEvent: String
)