package com.example.ecorouteapp.admin

data class MonitoringStation(
    val id: String ="",
    val name: String ="",
    val status: Boolean = false,
    val type: String ="",
    val latitude: String ="",
    val longitude: String ="",
    val created: String =""
)

data class Measurement(
    val sensorId: Int,
    val date: String,
    val time: String,
    val PM25: Double,
    val PM10: Double,
    val AQI: Int,
    val unit: String
)

data class AdminUser(
    val username: String,
    val email: String,
    val created: String,
    val updated: String,
    val roles: List<UserRole>,
    val permissions: List<String>,
    val isBlocked: Boolean = false
)