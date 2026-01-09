package com.example.ecorouteapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.ecorouteapp.admin.AdminScreen
import com.example.ecorouteapp.admin.StationDetailsScreen
import com.example.ecorouteapp.history.RouteDetailsScreen
import com.example.ecorouteapp.history.RouteHistoryScreen
import com.example.ecorouteapp.auth.login.LoginScreen
import com.example.ecorouteapp.auth.register.RegistrationScreen
import com.example.ecorouteapp.history.RouteViewModel
import com.example.ecorouteapp.monitor.AirQualityMonitorScreen
import com.example.ecorouteapp.report.ReportSensorDetailsScreen
import com.example.ecorouteapp.report.ReportSensorScreen
import com.example.ecorouteapp.settings.SettingsScreen
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])

    @OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController, viewModelFactory: AppViewModelFactory) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != "login" && currentRoute != "registration" && currentRoute != "report_sensor" && currentRoute != "report_sensor_details" && currentRoute?.startsWith("route_details") != true && currentRoute?.startsWith("station_details") != true) {
                Column {
                    TopAppBar(
                        title = { Text("Air Quality Monitor") },
                        actions = {
                            IconButton(onClick = { navController.navigate("report_sensor") }) {
                                Icon(Icons.Default.Warning, contentDescription = "Report Sensor")
                            }
                            IconButton(onClick = { navController.navigate("login") }) {
                                Icon(Icons.Default.Lock, contentDescription = "Logout")
                            }
                        }
                    )
                    NavigationBar(navController, currentRoute)
                }
            } else if (currentRoute == "report_sensor" || currentRoute == "report_sensor_details" || currentRoute?.startsWith("route_details") == true || currentRoute?.startsWith("station_details") == true) {
                TopAppBar(
                    title = { Text("Air Quality Monitor") },
                    actions = {
                        IconButton(onClick = { navController.navigate("login") }) {
                            Icon(Icons.Default.Lock, contentDescription = "Logout")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    goToHomePage = { navController.navigate("air_monitor") },
                    goToRegistrationPage = { navController.navigate("registration") }
                )
            }
            composable("registration") {
                RegistrationScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    goToLoginPage = { navController.navigate("login") },
                    goToHomeScreen = { navController.navigate("air_monitor") }
                )
            }
            composable("air_monitor") {
                AirQualityMonitorScreen(
                    viewModel = viewModel(factory = viewModelFactory),
                    onRouteFinish = {routeId -> navController.navigate("route_details/${routeId}") }
                )
            }
            composable("history") {
                RouteHistoryScreen(onRouteClick = { routeId -> navController.navigate("route_details/$routeId") })
            }
            composable("settings") {
                SettingsScreen()
            }
            composable("admin") {
                AdminScreen(onStationClick = { stationId -> navController.navigate("station_details/$stationId") })
            }
            composable("report_sensor") {
                ReportSensorScreen(onBack = { navController.popBackStack() }, onStationSelected = { navController.navigate("report_sensor_details") })
            }
            composable("report_sensor_details") {
                ReportSensorDetailsScreen(onBack = { navController.popBackStack() }, onSave={})
            }
            composable(
                route = "route_details/{routeId}",
                arguments = listOf(navArgument("routeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vm : RouteViewModel = viewModel<RouteViewModel>(factory = viewModelFactory)
                RouteDetailsScreen(
                    routeId = backStackEntry.arguments?.getString("routeId") ?: "",
                    onBack = { navController.popBackStack() },
                    viewModel = vm
                )
            }
            composable(
                route = "station_details/{stationId}",
                arguments = listOf(navArgument("stationId") { type = NavType.StringType })
            ) { backStackEntry ->
                StationDetailsScreen(
                    stationId = backStackEntry.arguments?.getString("stationId") ?: "",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun NavigationBar(navController: NavHostController, currentRoute: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem(icon = Icons.Default.Home, text = "Monitor", selected = currentRoute == "air_monitor", onClick = { navController.navigate("air_monitor") })
            NavigationItem(icon = Icons.Default.Search, text = "History", selected = currentRoute == "history", onClick = { navController.navigate("history") })
            NavigationItem(icon = Icons.Default.Settings, text = "Settings", selected = currentRoute == "settings", onClick = { navController.navigate("settings") })
            NavigationItem(icon = Icons.Default.Warning, text = "Admin", selected = currentRoute == "admin", onClick = { navController.navigate("admin") })
        }
    }
}

@Composable
fun NavigationItem(icon: ImageVector, text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = text)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 10.sp)
        }
    }
}
