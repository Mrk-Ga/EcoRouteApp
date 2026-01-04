package com.example.ecorouteapp.monitor

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.network.AirMonitorRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.time.TimeSource

class AirMonitorViewModel(private val repository: AirMonitorRepository): ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState

    private val _routeState = MutableStateFlow<RouteState>(RouteState.Idle)
    val routeState: StateFlow<RouteState> = _routeState

    //job to be able to stop monitoring
    private var observeJob: Job? = null

    fun startObserving(routeId: String) {

        _uiState.update { it.copy(id = UUID.randomUUID().toString()) }

        if (observeJob != null) return

        _routeState.value = RouteState.DuringMonitoring

        observeJob = viewModelScope.launch {
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
                            currentLocation =Pair( routeData.currentLocation.first(), routeData.currentLocation.last()),
                            alert = routeData.alert,
                            time = routeData.time
                        )
                    }
                    Log.d("AirMonitorViewModel", "Received route data: ${uiState}")
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
    val id: String = "",
    var PM25: Float = 0f,
    var PM10: Float = 0f,
    var AQI: Int = 0,
    var currentLocation: Pair<Float, Float> = Pair(0f,0f),
    var alert: String = "",
    var error: String? = null,
    var time:String = ""
)

sealed class RouteState {

    object Idle : RouteState()
    object DuringMonitoring: RouteState()

}

