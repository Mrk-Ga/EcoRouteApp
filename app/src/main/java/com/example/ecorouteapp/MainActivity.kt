package com.example.ecorouteapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.ecorouteapp.network.testServer.MockBackend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockWebServer

class MainActivity : ComponentActivity() {


    private val appContainer by lazy { AppContainer() }
    private val viewModelFactory by lazy {
        AppViewModelFactory(appContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            CoroutineScope(Dispatchers.IO).launch {
                MockBackend.start()
            }


        setContent {
            //EcoRouteApp()
            //LoginScreen()
            val navController = rememberNavController()
            AppNavHost(navController, viewModelFactory)
        }
    }
}
