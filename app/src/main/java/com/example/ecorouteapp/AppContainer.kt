package com.example.ecorouteapp

import com.example.ecorouteapp.monitor.AirMonitorRepository
import com.example.ecorouteapp.network.ApiService
import com.example.ecorouteapp.auth.AuthRepository
import com.example.ecorouteapp.history.RouteRepository
import com.example.ecorouteapp.network.RetrofitInstance
import com.example.ecorouteapp.report.ReportSensorRepository
import com.example.ecorouteapp.settings.SettingsRepository

class AppContainer {

    private val apiService: ApiService by lazy {
        RetrofitInstance.api
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(apiService)
    }

    val airMonitorRepository: AirMonitorRepository by lazy{
        AirMonitorRepository(apiService)
    }

    val routeRepository: RouteRepository by lazy{
        RouteRepository(apiService)
    }

    val reportSensorRepository: ReportSensorRepository by lazy{
        ReportSensorRepository(apiService)
    }

    val settingsRepository: SettingsRepository by lazy{
        SettingsRepository(apiService)
    }




}