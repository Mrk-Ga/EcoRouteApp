package com.example.ecorouteapp.admin.stations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecorouteapp.admin.MonitoringStation
import com.example.ecorouteapp.admin.users.StatusChip


@Composable
fun MonitoringStationsList(
    stations: List<MonitoringStation>,
    onStationClick: (String) -> Unit) {
/*    val stations = listOf(
        MonitoringStation("station-1", "Warsaw City Center", "active", "automatic", "52.229700, 21.012200", "23.11.2025"),
        MonitoringStation("station-2", "Praga District Monitor", "active", "automatic", "52.250300, 21.040300", "24.10.2025"),
        MonitoringStation("station-3", "Mokotów Air Station", "active", "mobile", "52.187200, 21.021200", "8.12.2025")
    )*/

    Column {
        Text(text = "Monitoring Stations", style = MaterialTheme.typography.headlineSmall)
        Text(text = "View and manage air quality monitoring stations", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(stations) { station ->
                StationItem(station = station, onClick = { onStationClick(station.id) })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationItem(station: MonitoringStation, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = station.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusChip(text = specifyStatus(station.status), color = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                StatusChip(text = station.type, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = station.latitude + ", " + station.longitude, color = Color.Gray)
            Text(text = "Created: ${station.created}", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onClick) {
                Icon(Icons.Default.Info, contentDescription = "Details")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Details")
            }
        }
    }
}
