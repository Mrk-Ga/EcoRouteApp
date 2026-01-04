package com.example.ecorouteapp

import com.example.ecorouteapp.network.AuthApi
import com.example.ecorouteapp.network.AuthRepository
import com.example.ecorouteapp.network.RetrofitInstance

class AppContainer {

    private val apiService: AuthApi by lazy {
        RetrofitInstance.api
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(apiService)
    }
}