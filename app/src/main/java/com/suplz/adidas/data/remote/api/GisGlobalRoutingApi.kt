package com.suplz.adidas.data.remote.api

import com.suplz.adidas.data.remote.dto.gis.GisGlobalRouteRequestDto
import com.suplz.adidas.data.remote.dto.gis.GisGlobalRouteResponseDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GisGlobalRoutingApi {
    @POST("routing/7.0.0/global")
    suspend fun getRoute(
        @Body body: GisGlobalRouteRequestDto,
        @Query("key") key: String = "783e0858-39de-4c83-a72c-bc2858c795be"
    ): GisGlobalRouteResponseDto
}