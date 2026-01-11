package com.example.ecorouteapp.settings

data class SettingsDataRequest(
    val userId:Int = 0,
    val timestamp: Long = 0,
    val locationDataCollection: Boolean = false,
    val airQualityDataCollection: Boolean =false,
    val marketingCommunications: Boolean = false
)

data class SettingsDataResponse(
    val locationDataCollection: Boolean = false,
    val airQualityDataCollection: Boolean = false,
    val marketingCommunications: Boolean = false
)