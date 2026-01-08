package com.example.ecorouteapp.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @GET("routes/{routeId}")
    suspend fun getRouteInfo(@Path("routeId") routeId: String): RouteData

    @POST("routes/{routeId}/location")
    suspend fun postLocationData(@Path("routeId") routeId:String, @Body location: LocationData): Response<Unit>

}


