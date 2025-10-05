@file:OptIn(ExperimentalTime::class)

package com.suplz.adidas.data.repository

import com.suplz.adidas.data.mapper.toDomain
import com.suplz.adidas.data.mapper.toRequestDto
import com.suplz.adidas.data.remote.api.TimelineApi
import com.suplz.adidas.data.remote.dto.TimelineRequestDto
import com.suplz.adidas.domain.entity.CalendarEvent
import com.suplz.adidas.domain.entity.SmartTimelineGeocoded
import com.suplz.adidas.domain.repository.TimelineRepository
import kotlinx.datetime.*
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TimelineRepositoryImpl @Inject constructor(
    private val api: TimelineApi // TimelineApi теперь будет возвращать SmartTimelineGeocodedDto
) : TimelineRepository {

    companion object {
        private const val HOME_NAME = "Дом"
        // Координаты дома теперь являются стартовой точкой для всех маршрутов
        private const val HOME_COORDINATES = "Москва,НабержнаяТарасаШевченко,29" // Новосибирск для примера
        private const val HOME_DEPARTURE_HOUR = 7
        private const val HOME_DEPARTURE_END_HOUR = 8
    }

    // Метод теперь возвращает события с координатами
    override suspend fun getGeocodedTimeline(events: List<CalendarEvent>): SmartTimelineGeocoded {
        val homeEvent = createHomeEvent()
        val allEvents = listOf(homeEvent) + events

        val requestBody = createTimelineRequest(allEvents)

        // 1. Делаем запрос на наш бэкенд
        val timelineGeocodedDto = api.getSmartTimeline(requestBody)

        // 2. Маппим DTO в доменную модель и возвращаем
        return timelineGeocodedDto.toDomain()
    }

    private fun createHomeEvent(): CalendarEvent {
        val systemTZ = TimeZone.currentSystemDefault()
        val startOfToday = Clock.System.todayIn(systemTZ).atStartOfDayIn(systemTZ)
        val startTime = startOfToday.plus(HOME_DEPARTURE_HOUR, DateTimeUnit.HOUR)
        val endTime = startOfToday.plus(HOME_DEPARTURE_END_HOUR, DateTimeUnit.HOUR)
        return CalendarEvent(
            title = HOME_NAME,
            locationText = HOME_COORDINATES, // Вставляем координаты дома
            startTime = startTime,
            endTime = endTime
        )
    }

    private fun createTimelineRequest(events: List<CalendarEvent>): TimelineRequestDto {
        val systemTZ = TimeZone.currentSystemDefault()
        val todayDate = Clock.System.todayIn(systemTZ)
        val tomorrowDate = todayDate.plus(1, DateTimeUnit.DAY)

        val todayEvents = events.filter { it.startTime.toLocalDateTime(systemTZ).date == todayDate }
        val tomorrowEvents = events.filter { it.startTime.toLocalDateTime(systemTZ).date == tomorrowDate }

        return TimelineRequestDto(
            today = todayEvents.map { it.toRequestDto() },
            tomorrow = tomorrowEvents.map { it.toRequestDto() }
        )
    }
}