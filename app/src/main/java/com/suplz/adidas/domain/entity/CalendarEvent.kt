@file:OptIn(ExperimentalTime::class)

package com.suplz.adidas.domain.entity

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class CalendarEvent(
    val title: String,
    val locationText: String?,
    val startTime: Instant,
    val endTime: Instant
)