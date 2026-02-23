package com.example.ecorouteapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecorouteapp.admin.AdminViewModel
import com.example.ecorouteapp.auth.SessionManager
import com.example.ecorouteapp.monitor.location.LocationRepository
import com.example.ecorouteapp.auth.login.LoginViewModel
import com.example.ecorouteapp.monitor.AirMonitorViewModel
import com.example.ecorouteapp.auth.register.RegistrationViewModel
import com.example.ecorouteapp.history.RouteViewModel
import com.example.ecorouteapp.report.ReportSensorViewModel
import com.example.ecorouteapp.settings.SettingsViewModel


class AppViewModelFactory(
    private val container: AppContainer,
    private val location: LocationRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(container.authRepository, sessionManager) as T

            modelClass.isAssignableFrom(RegistrationViewModel::class.java) ->
                RegistrationViewModel(container.authRepository, sessionManager) as T

            modelClass.isAssignableFrom(AirMonitorViewModel::class.java) ->
                AirMonitorViewModel(container.airMonitorRepository,sessionManager, location) as T

            modelClass.isAssignableFrom(RouteViewModel::class.java) ->
                RouteViewModel(container.routeRepository) as T

            modelClass.isAssignableFrom(ReportSensorViewModel::class.java) ->
                ReportSensorViewModel(container.reportSensorRepository, location) as T

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(container.settingsRepository, sessionManager) as T

            modelClass.isAssignableFrom(AdminViewModel::class.java) ->
                AdminViewModel(container.adminRepository) as T


            else -> throw IllegalArgumentException(
                "Unknown ViewModel: ${modelClass.name}"
            )
        }
    }
}