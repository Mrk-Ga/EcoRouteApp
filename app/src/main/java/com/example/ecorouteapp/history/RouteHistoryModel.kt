package com.example.ecorouteapp.history

data class RouteHistory(
    val date: String,
    val status: String,
    val time: String,
    val duration: String,
    val points: Int,
    val avgPm25: String,
    val maxPm25: String
)