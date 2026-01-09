package com.example.ecorouteapp.monitor

import com.example.ecorouteapp.monitor.location.LocationData
import com.example.ecorouteapp.network.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

/**
 *
 *     @GET("routes/{routeId}")
 *     suspend fun getRouteInfo(@Path("routeId") routeId: String): RouteData
 *
 *     @POST("routes/{routeId}/location")
 *     suspend fun postLocationData(@Path("routeId") routeId:String, @Body location: LocationData): Response<Unit>
 *
 *     @GET("/routes/next_id")
 *     suspend fun getRouteId():String
 *
 */

class AirMonitorRepository(private val apiService: ApiService) {

    fun observeRoute(routeId: String): Flow<RouteData> = flow {
        while (true) {
            val data = apiService.getRouteInfo(routeId)
            emit(data)
            delay(5_000)
        }
    }

    suspend fun sendLocationData(routeId:String, location: LocationData):Boolean {
            val data = apiService.postLocationData(routeId,location)
            return data.isSuccessful
    }

    suspend fun getRouteId(): ResponseRouteId {
        return apiService.getRouteId()
    }



}

data class ResponseRouteId(val routeId: String)



data class RouteData (val PM25:Float,
                      val PM10:Float,
                      val AQI:Int,
                      val alert: String,
                        val time: String)