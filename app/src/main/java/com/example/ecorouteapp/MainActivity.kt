package com.example.ecorouteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.ecorouteapp.auth.SessionManager
import com.example.ecorouteapp.monitor.location.LocationRepository
import com.example.ecorouteapp.network.testServer.MockBackend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    private val appContainer by lazy { AppContainer() }

    private val locationRepository by lazy { LocationRepository(application) }

    private val sessionManager by lazy {SessionManager()}
    private val viewModelFactory by lazy {
        AppViewModelFactory(appContainer, locationRepository, sessionManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

/*            CoroutineScope(Dispatchers.IO).launch {
                MockBackend.start()
            }*/

        setContent  {
            //EcoRouteApp()
            //LoginScreen()
            val navController = rememberNavController()

            AppNavHost(navController, viewModelFactory)
        }
    }
}
