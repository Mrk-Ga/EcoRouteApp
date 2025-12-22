package com.example.ecorouteapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirQualityMonitorScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Air Quality Monitor") },
                actions = {
                    Button(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Warning, contentDescription = "Report Sensor")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Report Sensor")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Lock, contentDescription = "Logout")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            item{
                Text("Welcome, Demo User", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item{
                NavigationBar()
                Spacer(modifier = Modifier.height(16.dp))
            }
            item{
                HealthAlert()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item{
                CurrentAirQuality()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item{
                RouteTracking()
            }

        }
    }
}

@Composable
fun NavigationBar() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem(icon = Icons.Default.Home, text = "Track Route", selected = true)
            NavigationItem(icon = Icons.Default.Search, text = "History")
            NavigationItem(icon = Icons.Default.Settings, text = "Settings")
            NavigationItem(icon = Icons.Default.Warning, text = "Admin")
        }
    }
}

@Composable
fun NavigationItem(icon: ImageVector, text: String, selected: Boolean = false) {
    Button(
        onClick = {/**/},
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text)
            //Spacer(modifier = Modifier.width(4.dp))
            //Text(text, fontSize = 12.sp)
        }
    }
}


@Composable
fun HealthAlert() {
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
fun CurrentAirQuality() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Icon(Icons.Default.Notifications, contentDescription = "Current Air Quality")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Current Air Quality", style = MaterialTheme.typography.titleLarge)
            }

            Text("Real-time air quality measurements", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("AQI", style = MaterialTheme.typography.titleMedium)
                    Text("Unhealthy (Sensitive)", fontSize = 12.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF57C00), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("112", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            AirQualityIndicator(label = "PM2.5", value = 45f, maxValue = 100f, unit = "µg/m³")
            Spacer(modifier = Modifier.height(8.dp))
            AirQualityIndicator(label = "PM10", value = 68f, maxValue = 100f, unit = "µg/m³")

            Spacer(modifier = Modifier.height(16.dp))
            Text("Updated: 22:20:10", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
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
fun RouteTracking() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
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
                Column(modifier = Modifier.padding(16.dp)){
                    Text("Current Location")
                    Text("52.229700, 21.012200", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO */ },
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

@Preview(showBackground = true)
@Composable
fun AirQualityMonitorScreenPreview() {
    MaterialTheme {
        AirQualityMonitorScreen()
    }
}
