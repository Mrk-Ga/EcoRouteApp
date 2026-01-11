package com.example.ecorouteapp.admin

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val repository: AdminRepository
): ViewModel() {

    private val _state = MutableStateFlow<AdminState>(AdminState.Idle)
    val state: StateFlow<AdminState> = _state


    private val _monitoringStations = MutableStateFlow<List<MonitoringStation>>(emptyList())
    val monitoringStations: StateFlow<List<MonitoringStation>> = _monitoringStations

    private val _stationDetails = MutableStateFlow<MonitoringStation>(MonitoringStation())
    val stationDetails: StateFlow<MonitoringStation> = _stationDetails

    private val _stationMeasurements = MutableStateFlow<List<Measurement>>(emptyList())
    val stationMeasurements: StateFlow<List<Measurement>> = _stationMeasurements



    fun postStationStatus(stationId:String,status:Boolean){
        viewModelScope.launch {
            repository.postStationStatus(stationId, status)
        }
    }

    fun getStationDetails(stationId:String) {
        /*viewModelScope.launch {
            val details = repository.getStationDetails(stationId)
            _stationDetails.value = details
        }*/

        for (station in monitoringStations.value!!) {
            if (station.id == stationId) {
                _stationDetails.value = station
                _state.value = AdminState.LoadedStationDetails
                break
            }
        }
    }

    fun getMonitoringStations() {
        viewModelScope.launch {
            val stations = repository.getMonitoringStations()
            _monitoringStations.value = stations
            _state.value = AdminState.LoadedMonitoringStations
        }
    }

    fun getStationMeasurements(stationId: String){
        viewModelScope.launch {
            val measurements = repository.getStationMeasurements(stationId)
            _stationMeasurements.value = measurements
            _state.value = AdminState.LoadedStationMeasurements
        }
    }

}

sealed class AdminState(){
    object Idle: AdminState()
    object LoadedMonitoringStations: AdminState()
    object LoadedStationDetails: AdminState()
    object LoadedStationMeasurements: AdminState()
}


