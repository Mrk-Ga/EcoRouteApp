package com.example.ecorouteapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecorouteapp.monitor.location.LocationRepository
import com.example.ecorouteapp.login.LoginViewModel
import com.example.ecorouteapp.monitor.AirMonitorViewModel
import com.example.ecorouteapp.register.RegistrationViewModel

class AppViewModelFactory(
    private val container: AppContainer,
    private val location: LocationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(container.authRepository) as T

            modelClass.isAssignableFrom(RegistrationViewModel::class.java) ->
                RegistrationViewModel(container.authRepository) as T

            modelClass.isAssignableFrom(AirMonitorViewModel::class.java) ->
                AirMonitorViewModel(container.airMonitorRepository, location) as T


            else -> throw IllegalArgumentException(
                "Unknown ViewModel: ${modelClass.name}"
            )
        }
    }
}