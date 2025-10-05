@file:OptIn(ExperimentalTime::class)

package com.suplz.adidas.data.local

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.suplz.adidas.domain.entity.CalendarEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class CalendarReader @Inject constructor(
    private val context: Context
) {

    companion object {
        private val EVENT_PROJECTION: Array<String> = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY
        )

        private const val PROJ_TITLE_INDEX = 0
        private const val PROJ_LOCATION_INDEX = 1
        private const val PROJ_DTSTART_INDEX = 2
        private const val PROJ_DTEND_INDEX = 3
    }

    suspend fun fetchEvents(): List<CalendarEvent> {
        return withContext(Dispatchers.IO) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALENDAR
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@withContext emptyList()
            }

            val timezone = TimeZone.currentSystemDefault()
            val today = Clock.System.todayIn(timezone)
            val startOfToday = today.atStartOfDayIn(timezone)
            val startOfDayAfterTomorrow = today
                .plus(2, DateTimeUnit.DAY)
                .atStartOfDayIn(timezone)

            val beginMillis = startOfToday.toEpochMilliseconds()
            val endMillis = startOfDayAfterTomorrow.toEpochMilliseconds()

            val selection = "(${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} < ?) AND ${CalendarContract.Events.ALL_DAY} = ?"
            val selectionArgs = arrayOf(beginMillis.toString(), endMillis.toString(), "0")
            val sortOrder = "${CalendarContract.Events.DTSTART} ASC"

            val events = mutableListOf<CalendarEvent>()

            context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                EVENT_PROJECTION,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    events.add(cursor.toCalendarEvent())
                }
            }

            events
        }
    }

    private fun Cursor.toCalendarEvent(): CalendarEvent {
        val title = getString(PROJ_TITLE_INDEX) ?: "Без названия"
        val location = getString(PROJ_LOCATION_INDEX)
        val startTimeMillis = getLong(PROJ_DTSTART_INDEX)
        val endTimeMillis = getLong(PROJ_DTEND_INDEX).takeIf { it > 0 } ?: startTimeMillis

        return CalendarEvent(
            title = title,
            locationText = location,
            startTime = Instant.fromEpochMilliseconds(startTimeMillis),
            endTime = Instant.fromEpochMilliseconds(endTimeMillis)
        )
    }
}