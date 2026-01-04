package com.example.ecorouteapp.monitor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AirQualityMonitorScreen(
    viewModel: AirMonitorViewModel
) {
    val monitorState by viewModel.uiState.collectAsState()
    val monitorRoute by viewModel.routeState.collectAsState()


    LazyColumn {
        item {
            HealthAlert(monitorState)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            CurrentAirQuality(
                            monitorState
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            RouteTracking(viewModel, monitorState, monitorRoute)
        }
    }
}



@Composable
fun HealthAlert(monitorState: RouteUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBE6))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = "Health Alert", tint = Color(0xFFF57C00))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Health Alert:", fontWeight = FontWeight.Bold, color = Color(0xFFF57C00))
                Text("Air quality levels exceed safe thresholds.", color = Color.Black)
            }
        }
    }
}

@Composable
fun CurrentAirQuality(
    monitorState: RouteUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        //val monitorState by viewModel.uiState.collectAsState()

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = "Current Air Quality")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Current Air Quality", style = MaterialTheme.typography.titleLarge)
            }

            Text("Real-time air quality measurements", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AQI", style = MaterialTheme.typography.titleMedium)
                    Text("Unhealthy (Sensitive)", fontSize = 12.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF57C00), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("${monitorState.AQI}", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            AirQualityIndicator(label = "PM2.5", value = monitorState.PM25, maxValue = 100f, unit = "µg/m³")
            Spacer(modifier = Modifier.height(8.dp))
            AirQualityIndicator(label = "PM10", value = monitorState.PM10, maxValue = 100f, unit = "µg/m³")

            Spacer(modifier = Modifier.height(16.dp))
            Text("Updated: ${monitorState.time}", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirQualityIndicator(label: String, value: Float, maxValue: Float, unit: String) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label)
            Text("$value $unit")
        }
        LinearProgressIndicator(
            progress = { value / maxValue },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color.Blue, // Adjust color as needed
        )
    }
}

@Composable
fun RouteTracking(viewModel: AirMonitorViewModel, monitorState: RouteUiState, routeState: RouteState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        //val monitorState by viewModel.uiState.collectAsState()
        //val routeState by viewModel.routeState.collectAsState()

        var seconds by remember { mutableStateOf(0) }
        var running by remember { mutableStateOf(false) }
        LaunchedEffect(running) {
            while (running) {
                delay(1000)
                seconds++
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Route Tracking")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Route Tracking", style = MaterialTheme.typography.titleLarge)
            }
            Text("Start tracking your route", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Location")
                    Text("${monitorState.currentLocation.first}  ${monitorState.currentLocation.second}", color = Color.Gray)

                    Spacer(modifier = Modifier.height(5.dp))
                    if(running) {
                        Text("Time")
                        Text(formatSeconds(seconds))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))



            if (routeState is RouteState.DuringMonitoring){
                Button(
                    onClick = { viewModel.stopObserving()
                                running = !running
                                seconds = 0
                                viewModel.generateRouteReport()
                              },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Stop Tracking")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stop Tracking", color = Color.White)
                }
            }
            else {
                Button(
                    onClick = { viewModel.startObserving("routeId")
                                running = !running },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start Tracking")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Tracking", color = Color.White)
                }
            }
        }
    }
}

fun formatSeconds(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}
/*
@Preview(showBackground = true)
@Composable
fun AirQualityMonitorScreenPreview() {
    MaterialTheme {
        AirQualityMonitorScreen()
    }
}

 */
