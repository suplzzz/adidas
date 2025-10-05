package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimelineRequestDto(
    val today: List<EventRequestDto>,
    val tomorrow: List<EventRequestDto>
)