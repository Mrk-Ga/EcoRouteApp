package com.example.ecorouteapp.history

import com.example.ecorouteapp.network.ApiService
import com.example.ecorouteapp.network.RouteDetailsBrief

/**
 *
 *     @GET("/history/routes")
 *     suspend fun getHistory():List<RouteDetailsBrief>
 *
 *     @DELETE("/details/{routeId}")
 *     suspend fun deleteRoute(@Path("routeId") routeId:String): Response<Unit>
 *
 *     @GET("/details/{routeId}")
 *     suspend fun getRouteDetails(@Path("routeId") routeId:String): RouteDetails
 *
 */

class RouteRepository(private val apiService: ApiService) {

    suspend fun getHistory():List<RouteHistory> {
        return apiService.getHistory()
    }

    suspend fun deleteRoute(routeId:String) {
        apiService.deleteRoute(routeId)
    }

    suspend fun getRouteDetails(routeId:String): RouteDetails {
        return apiService.getRouteDetails(routeId)
    }

}