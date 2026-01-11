package com.example.ecorouteapp.history


data class RouteHistory(
    val id: String = "",
    val date: String = "",
    val status: String = "",
    val time: String = "",
    val duration: String = "",
    val points: Int = 0,
    val avgPm25: String ="",
    val maxPm25: String = ""
)


data class RouteDetails(
    var id: String = "",
    var date: String = "",
    var time: String = "",
    var status: String = "",
    var duration: String = "",
    var dataPoints: Int = 0,
    var avgPm25: Float = 0.0f,
    var maxPm25: Float = 0.0f,
    var avgPm10: Float = 0.0f,
    var maxPm10: Float = 0.0f,
    var exposureLevel: String = "",
    var exposureAssessment: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var reportGeneratedTime: String = "",
    var healthRecommendations: List<String> = emptyList()

)