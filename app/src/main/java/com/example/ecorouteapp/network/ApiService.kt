package com.example.ecorouteapp.network

import com.example.ecorouteapp.admin.Measurement
import com.example.ecorouteapp.admin.MonitoringStation
import com.example.ecorouteapp.history.RouteDetails
import com.example.ecorouteapp.auth.LoginRequest
import com.example.ecorouteapp.auth.LoginResponse
import com.example.ecorouteapp.auth.RegisterRequest
import com.example.ecorouteapp.auth.RegisterResponse
import com.example.ecorouteapp.history.RouteHistory
import com.example.ecorouteapp.monitor.ResponseRouteId
import com.example.ecorouteapp.monitor.RouteData
import com.example.ecorouteapp.monitor.RouteStopRequest
import com.example.ecorouteapp.monitor.location.LocationData
import com.example.ecorouteapp.report.AvailableStationReport
import com.example.ecorouteapp.settings.SettingsDataRequest
import com.example.ecorouteapp.settings.SettingsDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // AUTH
    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    // ROUTES MONITORING
    @GET("/routes/{routeId}")
    suspend fun getRouteInfo(@Path("routeId") routeId: String): RouteData

    @POST("/routes/{routeId}/location")
    suspend fun postLocationData(@Path("routeId") routeId:String, @Body location: LocationData): Response<Unit>

    @POST ("/route/start_tracking")
    suspend fun postStartTracking(@Body userId:Int): ResponseRouteId

    @POST ("/route/stop_tracking")
    suspend fun postStopTracking(@Body stopRouteRequest: RouteStopRequest): Response<Unit>


    // HISTORY & DETAILS
    @GET("/history/routes")
    suspend fun getHistory():List<RouteHistory>

    @DELETE("/details/{routeId}")
    suspend fun deleteRoute(@Path("routeId") routeId:String): Response<Unit>

    @GET("/details/{routeId}")
    suspend fun getRouteDetails(@Path("routeId") routeId:String): RouteDetails


    // REPORT SENSORS
    @GET("/report/sensors/{location}")
    suspend fun getStationReport(@Path("location") location: String): List<AvailableStationReport> //lista stacji + pomiary stacji
    //format zmiennej --> location :String = {lat}_{lon}
    @POST("/report/sensors/{sensorId}")
    suspend fun postSensorReport(@Path("sensorId") sensorId:Int, @Body report: String): Response<Unit>


    // SETTINGS
    @GET("/settings/gdpr/{userId}")
    suspend fun getSettings(@Path("userId") userId:Int): SettingsDataResponse //

    @POST("/settings/gdpr/update")
    suspend fun postGDPR(@Body data: SettingsDataRequest): Response<Unit>


    // ADMIN

    @GET("/admin/stations")
    suspend fun getMonitoringStations(): List<MonitoringStation>

    @GET("/admin/stations/measurements/{stationId}")
    suspend fun getStationMeasurements(@Path("stationId") stationId:String): List<Measurement>

    @POST("/admin/stations/{stationId}/status")
    suspend fun postStationStatus(@Path("stationId") stationId:String, @Body status: Boolean): Response<Unit>


}




/*data class RouteDetailsBrief(
    val routeId: String,
    val date:String,
    val duration: Int,
    val waypointsCount: Int

)*/




