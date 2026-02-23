package com.example.ecorouteapp.report

import com.example.ecorouteapp.monitor.location.LocationData
import com.example.ecorouteapp.network.ApiService

/**
 *     @GET("/report/sensors")
 *     suspend fun getSensorReport(@Body location: LocationData):StationReport //lista stacji + pomiary stacji
 *
 *     @POST("/report/sensors/{sensorId}")
 *     suspend fun postSensorReport(@Path("sensorId") sensorId:Int, @Body report: String): Response<Unit>
 *
 */

class ReportSensorRepository(private val apiService: ApiService) {

    suspend fun getAvailableStations(location: String): List<AvailableStationReport> {
        return apiService.getStationReport(location)
    }

    suspend fun postSensorReport(sensorId: Int, report: String){
        apiService.postSensorReport(sensorId, report)
    }
}


