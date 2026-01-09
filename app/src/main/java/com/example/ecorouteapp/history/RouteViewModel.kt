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

    suspend fun getRouteDetails(routeId: String) {
        repository.getRouteDetails(routeId = routeId)
    }


}
