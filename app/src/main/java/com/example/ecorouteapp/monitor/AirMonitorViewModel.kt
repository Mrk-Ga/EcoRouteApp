package com.example.ecorouteapp.monitor

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.monitor.location.LocationData
import com.example.ecorouteapp.monitor.location.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class AirMonitorViewModel(
    private val repository: AirMonitorRepository,
    private val locationRepo: LocationRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState

    private val _routeState = MutableStateFlow<RouteState>(RouteState.Idle)
    val routeState: StateFlow<RouteState> = _routeState

    //job to be able to stop monitoring
    private var observeJob: Job? = null


    @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startObserving() {




        if (observeJob != null) return

        _routeState.value = RouteState.DuringMonitoring

        observeJob = viewModelScope.launch {

            val newRouteId = repository.getRouteId()
            _uiState.update { it.copy(routeId = newRouteId.routeId) }


            launch {
                repository.observeRoute(uiState.value.routeId)
                    .catch { e ->
                        _uiState.update { it.copy(error = e.message) }
                        Log.d("AirMonitorViewModel", "Error: ${e.message}")
                    }
                    .collect { routeData ->
                        _uiState.update {
                            it.copy(
                                PM25 = routeData.PM25,
                                PM10 = routeData.PM10,
                                AQI = routeData.AQI,
                                alert = routeData.alert,
                                time = routeData.time
                            )
                        }
                        Log.d("AirMonitorViewModel", "Received route data: ${uiState}")
                    }
            }

            launch {
                locationRepo.locationFlow(10_000)
                    .collect { (lat, lon) ->

                        _uiState.update {
                            it.copy(latitude = lat, longitude = lon)
                        }

                        repository.sendLocationData(
                            uiState.value.routeId,
                            LocationData(
                                latitude = lat,
                                longitude = lon,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
            }


        }
    }


    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    fun sendFinishRouteInformations() {
        viewModelScope.launch {
            val location = locationRepo.getSingleLocation()

            if (location == null) {
                Log.w("AirMonitorViewModel", "Brak lokalizacji – nie wysyłam danych końcowych")
                return@launch
            }

            try {
                repository.sendLocationData(
                    uiState.value.routeId,
                    LocationData(
                        latitude = location.first,
                        longitude = location.second,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.e("AirMonitorViewModel", "Błąd wysyłania końcowej lokalizacji", e)
            }
        }
    }




    fun stopObserving() {
        observeJob?.cancel()
        _routeState.value = RouteState.Idle
        observeJob = null
    }


}

data class RouteUiState(
    val routeId: String = "",
    var PM25: Float = 0f,
    var PM10: Float = 0f,
    var AQI: Int = 0,
    var alert: String = "",
    var error: String? = null,
    var time:String = "",
    var latitude: Double = 0.00,
    var longitude: Double = 0.00
)

sealed class RouteState {

    object Idle : RouteState()
    object DuringMonitoring: RouteState()

}

