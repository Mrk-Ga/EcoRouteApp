package com.example.ecorouteapp.network

import com.example.ecorouteapp.network.RetrofitInstance.api
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AirMonitorRepository(private val apiService: ApiService) {

    fun observeRoute(routeId: String): Flow<RouteData> = flow {
        while (true) {
            val data = apiService.getRouteInfo(routeId)
            emit(data)
            delay(5_000)
        }
    }


}

data class RouteData (val PM25:Float,
                      val PM10:Float,
                      val AQI:Int,
                      val currentLocation: List<Float>,
                      val alert: String,
                        val time: String)