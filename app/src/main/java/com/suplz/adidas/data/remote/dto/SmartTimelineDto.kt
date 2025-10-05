package com.suplz.adidas.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SmartTimelineDto(
    val today: List<TimelineItemDto>,
    val tomorrow: List<TimelineItemDto>
)