package com.example.ecorouteapp.history

import androidx.lifecycle.ViewModel
import com.example.ecorouteapp.monitor.RouteData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RouteViewModel (
    private val repository: RouteRepository
):ViewModel() {

    val _detailsUiState = MutableStateFlow(RouteDetails())
    val detailsUiState: StateFlow<RouteDetails> = _detailsUiState

    val _historyUiState = MutableStateFlow(listOf<RouteHistory>())
    val historyUiState: StateFlow<List<RouteHistory>> = _historyUiState


    suspend fun getRouteDetails(routeId: String) {
        val details = repository.getRouteDetails(routeId = routeId)
        _detailsUiState.value = details
    }

    suspend fun getHistory() {
        val history = repository.getHistory()
        _historyUiState.value = history
    }


}
