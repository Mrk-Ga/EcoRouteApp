package com.example.ecorouteapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecorouteapp.monitor.location.LocationRepository
import com.example.ecorouteapp.auth.login.LoginViewModel
import com.example.ecorouteapp.monitor.AirMonitorViewModel
import com.example.ecorouteapp.auth.register.RegistrationViewModel
import com.example.ecorouteapp.history.RouteViewModel
import com.example.ecorouteapp.report.ReportSensorViewModel

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

            modelClass.isAssignableFrom(RouteViewModel::class.java) ->
                RouteViewModel(container.routeRepository) as T

            modelClass.isAssignableFrom(ReportSensorViewModel::class.java) ->
                ReportSensorViewModel(container.reportSensorRepository, location) as T


            else -> throw IllegalArgumentException(
                "Unknown ViewModel: ${modelClass.name}"
            )
        }
    }
}