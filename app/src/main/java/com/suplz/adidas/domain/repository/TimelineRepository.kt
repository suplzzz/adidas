package com.suplz.adidas.domain.repository

import com.suplz.adidas.domain.entity.CalendarEvent
import com.suplz.adidas.domain.entity.SmartTimelineGeocoded // <-- Новый тип

interface TimelineRepository {
    // Сигнатура метода изменена: теперь он возвращает не финальный SmartTimeline,
    // а промежуточный объект SmartTimelineGeocoded
    suspend fun getGeocodedTimeline(events: List<CalendarEvent>): SmartTimelineGeocoded
}