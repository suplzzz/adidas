package com.suplz.adidas.domain.entity


data class SmartTimeline(
    val today: List<TimelineItem>,
    val tomorrow: List<TimelineItem>
)