package com.example.ecorouteapp.network

import com.example.ecorouteapp.history.RouteDetails
import com.example.ecorouteapp.auth.LoginRequest
import com.example.ecorouteapp.auth.LoginResponse
import com.example.ecorouteapp.auth.RegisterRequest
import com.example.ecorouteapp.auth.RegisterResponse
import com.example.ecorouteapp.history.RouteHistory
import com.example.ecorouteapp.monitor.ResponseRouteId
import com.example.ecorouteapp.monitor.RouteData
import com.example.ecorouteapp.monitor.location.LocationData
import com.example.ecorouteapp.report.AvailableStationReport
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // AUTH
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse


    // ROUTES MONITORING
    @GET("routes/{routeId}")
    suspend fun getRouteInfo(@Path("routeId") routeId: String): RouteData

    @POST("routes/{routeId}/location")
    suspend fun postLocationData(@Path("routeId") routeId:String, @Body location: LocationData): Response<Unit>

    @GET("/routes/next_id")
    suspend fun getRouteId(): ResponseRouteId


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
    //nie mogłem wysłać @Body(LocationData) w GET, a przekazywanie obiektu w @Path jest zgubne

    @POST("/report/sensors/{sensorId}")
    suspend fun postSensorReport(@Path("sensorId") sensorId:Int, @Body report: String): Response<Unit>


    // SETTINGS
    @GET("/settings/gdpr/{userId}")
    suspend fun getSettings(@Body userId:Int): SettingsDataResponse //

    @POST("/settings/gdpr/update")
    suspend fun postGDPR(@Body data: SettingsDataRequest): Response<Unit>

    //dodać show measurements i change status w panelu admin dla stacji
}
data class SettingsDataRequest(
    val userId:Int,
    val timestamp: Long,
    val locationDataCollection: Boolean,
    val airQualityDataCollection: Boolean,
    val marketingCommunications: Boolean
)

data class SettingsDataResponse(
    val locationDataCollection: Boolean,
    val airQualityDataCollection: Boolean,
    val marketingCommunications: Boolean
)

data class RouteDetailsBrief(
    val routeId: String,
    val date:String,
    val duration: Int,
    val waypointsCount: Int

)




