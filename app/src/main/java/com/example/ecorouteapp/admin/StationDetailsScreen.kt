package com.example.ecorouteapp.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StationDetails(
    val id: String,
    val name: String,
    val type: String,
    val latitude: String,
    val longitude: String,
    val created: String,
    val status: String
)

val sampleStationDetails = StationDetails(
    id = "station-1",
    name = "Warsaw City Center",
    type = "Automatic",
    latitude = "52.229700",
    longitude = "21.012200",
    created = "23.11.2025",
    status = "Active"
)

@Composable
fun StationDetailsScreen(stationId: String, onBack: () -> Unit) {
    // In a real app, fetch details based on stationId
    val station = sampleStationDetails

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Back to Stations")
        }

        StationDetailsCard(station = station)

        OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.List, contentDescription = "Show Measurements")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Show Measurements")
        }

        OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Create, contentDescription = "Change Status")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Change Status")
        }
    }
}

@Composable
fun StationDetailsCard(station: StationDetails) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(station.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Station Details", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black,
                    contentColor = Color.White
                ) {
                    Text(
                        text = station.status.lowercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            DetailItem(label = "Station ID", value = station.id)
            DetailItem(label = "Type", value = station.type)
            DetailItem(label = "Latitude", value = station.latitude)
            DetailItem(label = "Longitude", value = station.longitude)
            DetailItem(label = "Created", value = station.created)
            DetailItem(label = "Status", value = station.status)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun StationDetailsScreenPreview() {
    MaterialTheme {
        StationDetailsScreen(stationId = "station-1", onBack = {})
    }
}
