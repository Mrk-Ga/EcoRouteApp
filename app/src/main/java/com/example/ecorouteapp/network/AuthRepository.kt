package com.example.ecorouteapp.network

class AuthRepository(private val authApi: AuthApi) {
    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return authApi.login(loginRequest)
    }
}