package com.suplz.adidas.data.repository

import com.suplz.adidas.data.local.CalendarReader
import com.suplz.adidas.domain.entity.CalendarEvent
import com.suplz.adidas.domain.repository.CalendarRepository
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val calendarReader: CalendarReader
) : CalendarRepository {

    override suspend fun getCalendarEvents(): List<CalendarEvent> {
        return calendarReader.fetchEvents()
    }
}