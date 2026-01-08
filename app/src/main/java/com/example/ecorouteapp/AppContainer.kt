package com.example.ecorouteapp

import com.example.ecorouteapp.network.AirMonitorRepository
import com.example.ecorouteapp.network.ApiService
import com.example.ecorouteapp.network.AuthRepository
import com.example.ecorouteapp.network.RetrofitInstance

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


}