package com.example.ecorouteapp.history


data class RouteHistory(
    val id: String,
    val date: String,
    val status: String,
    val time: String,
    val duration: String,
    val points: Int,
    val avgPm25: String,
    val maxPm25: String
)


data class RouteDetails(
    val id: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "",
    val duration: String = "",
    val dataPoints: Int = 0,
    val avgPm25: Float = 0.0f,
    val maxPm25: Float = 0.0f,
    val avgPm10: Float = 0.0f,
    val maxPm10: Float = 0.0f,
    val exposureLevel: String = "",
    val exposureAssessment: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val reportGeneratedTime: String = "",
    val healthRecommendations: List<String> = emptyList()

)