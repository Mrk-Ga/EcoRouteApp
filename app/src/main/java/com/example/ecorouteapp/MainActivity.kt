package com.example.ecorouteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {


    private val appContainer by lazy { AppContainer() }
    private val viewModelFactory by lazy {
        AppViewModelFactory(appContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //EcoRouteApp()
            //LoginScreen()
            val navController = rememberNavController()
            AppNavHost(navController, viewModelFactory)
        }
    }
}
