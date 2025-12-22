package com.example.ecorouteapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ecorouteapp.login.LoginScreen
import com.example.ecorouteapp.ui.AirQualityMonitorScreen

@Composable
fun AppNavHost(navContoller: NavHostController) {

   NavHost(
       navController = navContoller,
       startDestination = "air_monitor"
   ){

       composable(route = "login") {
           LoginScreen(
               goToHomePage = {
                   navContoller.navigate("air_monitor")
               }
           )
       }


       composable(route = "air_monitor") {
           AirQualityMonitorScreen()
       }



    }

}

