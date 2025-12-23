package com.example.ecorouteapp.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class AvailableStation(val name: String, val sensorCount: Int)

@Composable
fun ReportSensorScreen(onBack: () -> Unit) {
    val availableStations = listOf(
        AvailableStation("Warsaw City Center", 4),
        AvailableStation("Praga District Monitor", 4),
        AvailableStation("Mokotów Air Station", 4)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth()){
                Button(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Report Inaccurate Sensor", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Select a monitoring station on the map to report sensor issues", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Map Placeholder", tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Text("Interactive Map", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Click on a station marker to select", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                    // Placeholder pins
                    Box(modifier = Modifier.offset(x = (-80).dp, y = (-50).dp)) { Pin() }
                    Box(modifier = Modifier.offset(x = 80.dp, y = (-20).dp)) { Pin() }
                    Box(modifier = Modifier.offset(x = 20.dp, y = 80.dp)) { Pin() }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text("Available Stations:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(availableStations) { station ->
            AvailableStationItem(station = station)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun Pin() {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(Color.Blue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = "Map Pin", tint = Color.White)
    }
}

@Composable
fun AvailableStationItem(station: AvailableStation) {
    OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Station")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = station.name, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "(${station.sensorCount} sensors)", color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportSensorScreenPreview() {
    MaterialTheme {
        ReportSensorScreen(onBack = {})
    }
}
