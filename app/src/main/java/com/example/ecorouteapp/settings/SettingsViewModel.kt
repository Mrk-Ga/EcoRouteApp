package com.example.ecorouteapp.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecorouteapp.auth.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel (
    private val repository: SettingsRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(SettingsDataResponse())
    val uiState: StateFlow<SettingsDataResponse> = _uiState

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val state: StateFlow<SettingsState> = _state



    suspend fun getSettings() {
        val userId = sessionManager.session.value?.userId ?: return
        val settings = repository.getSettings(userId)
        _uiState.value = settings
        Log.d("SettingsViewModel", "Settings: ${uiState.value.toString()}")
        _state.value = SettingsState.Loaded
    }
    fun postSettings(locationDataCollection: Boolean,
                             airQualityDataCollection: Boolean,
                             marketingCommunications: Boolean){

        viewModelScope.launch {

            val data = SettingsDataRequest(
                sessionManager.session.value?.userId ?: return@launch,
                System.currentTimeMillis(),
                locationDataCollection,
                airQualityDataCollection,
                marketingCommunications

            )
            repository.postSettings(data)
        }


    }

}

sealed class SettingsState {
    object Idle : SettingsState()
    object Loaded : SettingsState()
}