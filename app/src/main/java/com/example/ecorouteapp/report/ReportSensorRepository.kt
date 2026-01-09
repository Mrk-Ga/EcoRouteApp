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

    suspend fun getReport(location: LocationData): StationReport {
        return apiService.getStationReport(location)
    }
}


data class Sensor(
    val pollutionType:String,
    val lastReading:Float,
    val sensorId:Int
)


data class StationReport(
    val sensors:List<Sensor>,
    val name: String,
    val distance: Double
)