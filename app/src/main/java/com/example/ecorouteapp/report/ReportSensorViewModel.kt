package com.example.ecorouteapp.report

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.monitor.location.LocationData
import com.example.ecorouteapp.monitor.location.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportSensorViewModel(
    private val repository: ReportSensorRepository,
    private val locationRepository: LocationRepository
): ViewModel() {

    private val _availableStationsUiState =
        MutableStateFlow<List<AvailableStationReport>>(emptyList())

    val availableStationsUiState: StateFlow<List<AvailableStationReport>> =
        _availableStationsUiState.asStateFlow()


    val _selectedStationUiState = MutableStateFlow(AvailableStationReport())
    val selectedStationUiState: StateFlow<AvailableStationReport> = _selectedStationUiState

    val _sensorReportUiState = mutableListOf<SensorReportData>()
    val sensorReportUiState: MutableList<SensorReportData> = _sensorReportUiState


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getAvailableStations(){
        viewModelScope.launch {
            val location = locationRepository.getSingleLocation()

/*            val locationData: LocationData =
                LocationData(System.currentTimeMillis(),
                location!!.first, location.second)*/
            val locationData = location!!.first.toString() + "_" + location.second.toString()

            _availableStationsUiState.value = repository.getAvailableStations(locationData)
        }
        Log.d("availableStations", _availableStationsUiState.value.toString())
    }

    fun setSelectedStation(stationName: String){
        //Log.d("selectedStation", station.toString())

        for(station in _availableStationsUiState.value){
            if(station.name == stationName){
                _selectedStationUiState.value = station
            }
        }
        //Log.d("availableStations", _availableStationsUiState.value.toString())
        Log.d("selectedStation", _selectedStationUiState.value.toString())
    }


    fun postSensorReport() {
        for (report in sensorReportUiState) {
            viewModelScope.launch {
                repository.postSensorReport(report.sensorId, report.report)
            }
        }
    }

    fun updateReportData(sensorId: Int, report:String){
        for (reportData in sensorReportUiState) {
            if (reportData.sensorId == sensorId) {
                reportData.report = report
                return
            }
        }
        sensorReportUiState.add(SensorReportData(sensorId, report))
    }

}