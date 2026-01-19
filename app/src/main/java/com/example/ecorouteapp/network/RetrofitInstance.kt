package com.example.ecorouteapp.network

import android.util.Log
import com.example.ecorouteapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    //private const val BASE_URL = "http://10.0.2.2:8080/"
    //private const val BASE_URL = "http://192.168.1.241:8081/"
    //private const val BASE_URL = "http://localhost:8081/"

    const val BASE_URL:String  = BuildConfig.BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

/*    val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("HTTP", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }*/

    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}

