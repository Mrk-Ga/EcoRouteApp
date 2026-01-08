package com.example.ecorouteapp.monitor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.monitor.location.LocationRepository
import com.example.ecorouteapp.network.AirMonitorRepository
import com.example.ecorouteapp.network.LocationData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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
    fun startObserving(routeId: String) {

        _uiState.update { it.copy(routeId = UUID.randomUUID().toString()) }

        if (observeJob != null) return

        _routeState.value = RouteState.DuringMonitoring

        observeJob = viewModelScope.launch {

            launch {
                repository.observeRoute(routeId)
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

                                /*currentLocation = Pair(
                                    routeData.currentLocation.first(),
                                    routeData.currentLocation.last()
                                ),*/
                                alert = routeData.alert,
                                time = routeData.time
                            )
                        }
                        Log.d("AirMonitorViewModel", "Received route data: ${uiState}")
                    }
            }

            launch {
                while (isActive) {
                    val location = locationRepo.getLocationSuspend()
                        ?: continue

                    _uiState.update {
                        it.copy(
                            latitude = location.first,
                            longitude = location.second
                        )
                    }

                    repository.sendLocation(
                        routeId = routeId,
                        LocationData(
                            latitude = location.first,
                            longitude = location.second
                        )
                    )

                    delay(10_000)
                }
            }


        }
    }

    fun stopObserving() {
        observeJob?.cancel()
        _routeState.value = RouteState.Idle
        observeJob = null
    }

    fun generateRouteReport(){

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

