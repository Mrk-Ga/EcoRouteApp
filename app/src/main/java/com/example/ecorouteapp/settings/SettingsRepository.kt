package com.example.ecorouteapp.settings

import com.example.ecorouteapp.network.ApiService
import com.example.ecorouteapp.report.AvailableStationReport
import retrofit2.Response

class SettingsRepository(private val apiService: ApiService) {

    suspend fun getSettings(userId: Int): SettingsDataResponse {
        return apiService.getSettings(userId)
    }
    suspend fun postSettings(data: SettingsDataRequest): Boolean {
        if(apiService.postGDPR(data).isSuccessful)
            return true
        return false
    }
}
