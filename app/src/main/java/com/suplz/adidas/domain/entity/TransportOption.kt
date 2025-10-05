package com.suplz.adidas.domain.entity


data class TransportOption(
    val type: TransportType,
    val duration: String,
    val status: RecommendationStatus
)