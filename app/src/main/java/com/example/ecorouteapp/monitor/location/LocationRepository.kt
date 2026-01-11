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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
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
        suspendCancellableCoroutine { cont ->

            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setMaxUpdateAgeMillis(5_000) // pozwala użyć cache
                .build()

            fused.getCurrentLocation(request, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(
                            location.latitude to location.longitude,
                            onCancellation = null
                        )
                    } else {
                        // fallback → lastLocation
                        fused.lastLocation
                            .addOnSuccessListener { last ->
                                if (last != null) {
                                    cont.resume(
                                        last.latitude to last.longitude,
                                        onCancellation = null
                                    )
                                } else {
                                    cont.resume(null, onCancellation = null)
                                }
                            }
                            .addOnFailureListener {
                                cont.resume(null, onCancellation = null)
                            }
                    }
                }
                .addOnFailureListener {
                    cont.resume(null, onCancellation = null)
                }

            // anulowanie coroutine → anuluj zapytanie
            cont.invokeOnCancellation {
                // getCurrentLocation nie wymaga ręcznego cleanupu,
                // ale zostawiamy hook na przyszłość
            }
        }
}
