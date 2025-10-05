package com.suplz.adidas.domain.usecase

import android.util.Log
import com.suplz.adidas.domain.entity.*
import com.suplz.adidas.domain.repository.CalendarRepository
import com.suplz.adidas.domain.repository.DirectionsRepository
import com.suplz.adidas.domain.repository.TimelineRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import javax.inject.Inject
import kotlin.math.roundToLong
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GetSmartTimelineUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val timelineRepository: TimelineRepository,
    private val directionsRepository: DirectionsRepository
) {

    companion object {
        private const val HOME_COORDINATES = "37.535921,55.743676"
        private const val HOME_DEPARTURE_HOUR = 7
    }

    suspend fun invoke(): SmartTimeline = coroutineScope {
        val localEvents = calendarRepository.getCalendarEvents()
        val geocodedTimeline = timelineRepository.getGeocodedTimeline(localEvents)

        Log.d("MyTimeline", "UseCase: JSON от бэкенда успешно получен и распарсен. Данные: $geocodedTimeline")

        val sortedTodayEvents = geocodedTimeline.today.sortedBy { it.startTime }
        val sortedTomorrowEvents = geocodedTimeline.tomorrow.sortedBy { it.startTime }

        val todayItems = buildTimelineForDay(sortedTodayEvents, isToday = true)
        val tomorrowItems = buildTimelineForDay(sortedTomorrowEvents, isToday = false)

        val finalResult = SmartTimeline(today = todayItems, tomorrow = tomorrowItems)

        Log.d("MyTimelineFinal", "UseCase ЗАВЕРШИЛ РАБОТУ. Финальные данные для UI: $finalResult")

        return@coroutineScope finalResult
    }

    private suspend fun buildTimelineForDay(
        events: List<GeocodedEvent>,
        isToday: Boolean
    ): List<TimelineItem> {
        val items = mutableListOf<TimelineItem>()
        var lastCoords = HOME_COORDINATES
        val systemTZ = TimeZone.currentSystemDefault()
        val today = Clock.System.todayIn(systemTZ)
        val dayStart = if (isToday) today else today.plus(1, DateTimeUnit.DAY)

        var lastAvailableTime = dayStart.atStartOfDayIn(systemTZ)
            .plus(HOME_DEPARTURE_HOUR, DateTimeUnit.HOUR)

        for (event in events) {
            val timelineItem = enrichEventWithRoutes(
                event = event,
                startCoords = lastCoords,
                departureTime = lastAvailableTime
            )
            if (timelineItem != null) {
                items.add(timelineItem)
                lastCoords = event.eventLocationCoords
                lastAvailableTime = event.endTime
            }
        }
        return items
    }

    private suspend fun enrichEventWithRoutes(
        event: GeocodedEvent,
        startCoords: String,
        departureTime: Instant
    ): TimelineItem? = coroutineScope {

        Log.d("MyTimeline", "--------------------------------")
        Log.d("MyTimeline", "enrichEvent: Начинаю обработку события '${event.name}'")
        Log.d("MyTimeline", "enrichEvent: Маршрут ИЗ: $startCoords")
        Log.d("MyTimeline", "enrichEvent: Маршрут КУДА: ${event.eventLocationCoords}")
        Log.d("MyTimeline", "enrichEvent: Время выезда: $departureTime")

        val allTransportTypes = listOf(
            TransportType.CAR, TransportType.BUS, TransportType.FOOT, TransportType.TAXI, TransportType.SCOOTER
        )

        val initialOptions = allTransportTypes.mapNotNull { type ->
            val durationInSeconds = directionsRepository.getRouteDurationSeconds(
                fromCoords = startCoords,
                toCoords = event.eventLocationCoords,
                type = type,
                departureTime = departureTime
            )

            Log.d("MyTimeline", "enrichEvent: -> 2GIS запрос для '$type': результат = $durationInSeconds секунд")

            durationInSeconds?.let { Pair(type, it) }
        }

        Log.d("MyTimeline", "enrichEvent: Для '${event.name}' получено ${initialOptions.size} вариантов маршрута от 2GIS.")

        if (initialOptions.isEmpty()) {
            Log.e("MyTimeline", "enrichEvent: ВСЕ запросы к 2GIS для '${event.name}' провалились! Возвращаю null.")
            return@coroutineScope null
        }

        var calculatedOptions = initialOptions.map { (type, durationSeconds) ->
            val arrivalTime = departureTime + durationSeconds.seconds
            val status = if (arrivalTime > event.startTime) RecommendationStatus.RED else RecommendationStatus.GRAY
            Triple(type, durationSeconds, status)
        }

        val suitableOptions = calculatedOptions.filter { it.third != RecommendationStatus.RED }
        if (suitableOptions.isNotEmpty()) {
            val isBadWeather = hasBadWeather(event.weather)
            val bestOption = suitableOptions.minByOrNull { (type, durationSeconds, _) ->
                var score = durationSeconds
                if (isBadWeather && (type == TransportType.FOOT || type == TransportType.SCOOTER)) {
                    score += 3600
                }
                score
            }
            bestOption?.let { best ->
                calculatedOptions = calculatedOptions.map {
                    if (it.first == best.first) it.copy(third = RecommendationStatus.GREEN) else it
                }
            }
        }

        val finalTransportOptions = calculatedOptions.map { (type, durationSeconds, status) ->
            TransportOption(type, formatDuration(durationSeconds), status)
        }

        val mainTravelDuration = finalTransportOptions.firstOrNull { it.status == RecommendationStatus.GREEN }?.duration
            ?: finalTransportOptions.firstOrNull { it.type == TransportType.CAR }?.duration
            ?: finalTransportOptions.firstOrNull()?.duration
            ?: "N/A"

        return@coroutineScope TimelineItem(
            name = event.name,
            userLocation = startCoords,
            eventLocation = event.eventLocationCoords,
            travelDuration = mainTravelDuration,
            transportOptions = finalTransportOptions,
            weather = event.weather
        )
    }

    private fun hasBadWeather(weather: WeatherInfo): Boolean {
        val badWeatherKeywords = listOf("дождь", "снег", "ливень", "гроза", "метель", "град", "rain")
        return badWeatherKeywords.any { weather.condition.contains(it, ignoreCase = true) }
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = (seconds / 60.0).roundToLong()
        return "$minutes мин"
    }
}