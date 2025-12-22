package com.example.ecorouteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //EcoRouteApp()
            //LoginScreen()
            val navController = rememberNavController()
            AppNavHost(navController)
        }
    }
}
