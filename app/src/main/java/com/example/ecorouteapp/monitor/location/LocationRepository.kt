package com.example.ecorouteapp.monitor.location

import android.Manifest
import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class LocationData(val timestamp:Long,val latitude: Double, val longitude: Double)

class LocationRepository(application: Application) {

    private val fused =
        LocationServices.getFusedLocationProviderClient(application)


    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    fun locationFlow(): Flow<Pair<Double, Double>> =
        callbackFlow {

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1_000L // jak najczęściej
            ).build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let {
                        trySend(it.latitude to it.longitude)
                    }
                }
            }

            fused.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )

            awaitClose { fused.removeLocationUpdates(callback) }
        }


    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    suspend fun getSingleLocation(): Pair<Double, Double>? =
        withTimeoutOrNull(5_000) { // ⏱ max 5s
            coroutineScope {

                val lastLocationDeferred = async {
                    fused.lastLocation.await()?.let {
                        it.latitude to it.longitude
                    }
                }

                val currentLocationDeferred = async {
                    val request = CurrentLocationRequest.Builder()
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build()

                    fused.getCurrentLocation(request, null).await()?.let {
                        it.latitude to it.longitude
                    }
                }

                select<Pair<Double, Double>?> {
                    lastLocationDeferred.onAwait { it }
                    currentLocationDeferred.onAwait { it }
                }
            }
        }
}
