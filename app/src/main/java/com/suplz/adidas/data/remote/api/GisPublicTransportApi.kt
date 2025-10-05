package com.suplz.adidas.data.remote.api

import com.suplz.adidas.data.remote.dto.gis.GisPublicTransportRequestDto
import com.suplz.adidas.data.remote.dto.gis.GisPublicTransportResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GisPublicTransportApi {
    @POST("public_transport/2.0/routes_search")
    suspend fun getRoute(
        @Body body: GisPublicTransportRequestDto,
        @Query("key") key: String = "783e0858-39de-4c83-a72c-bc2858c795be"
    ): GisPublicTransportResponseDto

}