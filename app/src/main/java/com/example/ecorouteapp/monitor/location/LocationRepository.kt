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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationRepository (
    val application: Application
){


    @RequiresPermission(
        allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION]
    )
    suspend fun getLocationSuspend(): Pair<Double, Double>? = suspendCoroutine { cont ->
        //val context = getApplication<Application>().applicationContext
        val context = application.applicationContext
        val fused = LocationServices.getFusedLocationProviderClient(context)

        Log.d("LOCATION", "Pobieranie lokalizacji (try: current → last → updates)")

        // 1️⃣ najpierw getCurrentLocation()
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .build()

        fused.getCurrentLocation(request, null)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    Log.d("LOCATION", "getCurrentLocation: ${loc.latitude}, ${loc.longitude}")
                    cont.resume(Pair(loc.latitude, loc.longitude))
                } else {
                    Log.w("LOCATION", "⚠getCurrentLocation zwrócił null, próbuję lastLocation...")

                    // 2️⃣ fallback na lastLocation
                    fused.lastLocation
                        .addOnSuccessListener { last ->
                            if (last != null) {
                                Log.d(
                                    "LOCATION",
                                    "lastLocation: ${last.latitude}, ${last.longitude}"
                                )
                                cont.resume(Pair(last.latitude, last.longitude))
                            } else {
                                Log.w(
                                    "LOCATION",
                                    "⚠lastLocation == null, próbuję requestLocationUpdates..."
                                )

                                // 3️⃣ awaryjnie wymuszamy 1 aktualizację
                                val requestLoc = LocationRequest.Builder(
                                    Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000L
                                ).setMaxUpdates(1).build()

                                val callback = object : LocationCallback() {
                                    override fun onLocationResult(result: LocationResult) {
                                        val location = result.lastLocation
                                        if (location != null) {
                                            Log.d(
                                                "LOCATION",
                                                "requestLocationUpdates: ${location.latitude}, ${location.longitude}"
                                            )
                                            cont.resume(Pair(location.latitude, location.longitude))
                                        } else {
                                            Log.e(
                                                "LOCATION",
                                                "Nie udało się uzyskać lokalizacji nawet po requestLocationUpdates"
                                            )
                                            cont.resume(null)
                                        }
                                        fused.removeLocationUpdates(this)
                                    }
                                }

                                fused.requestLocationUpdates(
                                    requestLoc,
                                    callback,
                                    Looper.getMainLooper()
                                )
                            }
                        }
                        .addOnFailureListener {
                            Log.e("LOCATION", "lastLocation error: ${it.message}")
                            cont.resume(null)
                        }
                }
            }
            .addOnFailureListener {
                Log.e("LOCATION", "getCurrentLocation error: ${it.message}")
                cont.resume(null)
            }
    }
}