package com.example.ecorouteapp.network

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return apiService.login(loginRequest)
    }

    suspend fun register(registerRequest: RegisterRequest): RegisterResponse {
        return apiService.register(registerRequest)
    }
}

data class LoginResponse(val accessToken: String)

data class LoginRequest(val email: String, val password: String)

data class RegisterResponse(val accessMessage: String)

data class RegisterRequest(val username:String, val email: String, val password: String)


