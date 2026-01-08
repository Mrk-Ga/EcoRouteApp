package com.example.ecorouteapp.network

import android.app.Application
import android.os.Looper
import android.util.Log
import com.example.ecorouteapp.network.RetrofitInstance.api
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AirMonitorRepository(private val apiService: ApiService) {

    fun observeRoute(routeId: String): Flow<RouteData> = flow {
        while (true) {
            val data = apiService.getRouteInfo(routeId)
            emit(data)
            delay(5_000)
        }
    }

    suspend fun sendLocation(routeId:String, location: LocationData):Boolean {
            val data = apiService.postLocationData(routeId,location)
            return data.isSuccessful
    }


}

data class LocationData(val latitude: Double, val longitude: Double)


data class RouteData (val PM25:Float,
                      val PM10:Float,
                      val AQI:Int,
                      val alert: String,
                        val time: String)