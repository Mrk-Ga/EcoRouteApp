package com.example.ecorouteapp.admin

data class MonitoringStation(
    val name: String,
    val status: String,
    val type: String,
    val location: String,
    val created: String
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