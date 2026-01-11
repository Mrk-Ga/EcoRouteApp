package com.example.ecorouteapp.admin

import com.example.ecorouteapp.network.ApiService

class AdminRepository(
    private val apiService: ApiService
) {

/*    suspend fun getStationDetails(stationId: String): MonitoringStation {
        return apiService.getStationDetails(stationId)
    }*/
    suspend fun postStationStatus(stationId: String, status: Boolean) {
        apiService.postStationStatus(stationId, status)
    }

    suspend fun getMonitoringStations(): List<MonitoringStation> {
        return apiService.getMonitoringStations()
    }

    suspend fun getStationMeasurements(stationId: String): List<Measurement> {
        return apiService.getStationMeasurements(stationId)
    }




}