package com.suplz.adidas.domain.repository

import com.suplz.adidas.domain.entity.CalendarEvent

interface CalendarRepository {

    suspend fun getCalendarEvents() : List<CalendarEvent>
}