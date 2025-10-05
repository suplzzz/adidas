package com.suplz.adidas.data.remote.api

import com.suplz.adidas.data.remote.dto.SmartTimelineGeocodedDto // <-- Новый DTO
import com.suplz.adidas.data.remote.dto.TimelineRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TimelineApi {
    @POST("adidas")
    suspend fun getSmartTimeline(@Body timelineRequest: TimelineRequestDto): SmartTimelineGeocodedDto
}