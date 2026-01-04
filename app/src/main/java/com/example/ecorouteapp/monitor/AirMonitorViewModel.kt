package com.example.ecorouteapp.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.network.AirMonitorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

class AirMonitorViewModel(private val repository: AirMonitorRepository): ViewModel() {

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState

    private val _routeState = MutableStateFlow<RouteState>(RouteState.Idle)
    val routeState: StateFlow<RouteState> = _routeState

    fun startObserving(routeId: String) {
        viewModelScope.launch {
            _routeState.value = RouteState.DuringMonitoring
            repository.observeRoute(routeId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                    _routeState.value = RouteState.Idle
                }
                .collect { routeData ->
                    _uiState.update {
                        it.copy(
                            PM25 = routeData.PM25,
                            PM10 = routeData.PM10,
                            AQI = routeData.AQI,
                            currentLocation = routeData.currentLocation,
                            alert = routeData.alert
                        )
                    }
                }
        }
    }
}

data class RouteUiState(
    val PM25: Float = 0f,
    val PM10: Float = 0f,
    val AQI: Int = 0,
    val currentLocation: Pair<Float, Float> = Pair(0f, 0f),
    val alert: String = "",
    val error: String? = null,
    val time:String = ""
)

sealed class RouteState {

    object Idle : RouteState()
    object DuringMonitoring: RouteState()

}

