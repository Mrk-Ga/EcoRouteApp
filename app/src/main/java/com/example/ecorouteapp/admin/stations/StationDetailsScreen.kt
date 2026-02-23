package com.example.ecorouteapp.admin.stations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecorouteapp.admin.AdminState
import com.example.ecorouteapp.admin.AdminViewModel
import com.example.ecorouteapp.admin.Measurement
import com.example.ecorouteapp.admin.MonitoringStation


/*val sampleStationDetails = StationDetails(
    id = "station-1",
    name = "Warsaw City Center",
    type = "Automatic",
    latitude = "52.229700",
    longitude = "21.012200",
    created = "23.11.2025",
    status = "Active"
)*/

fun specifyStatus(status: Boolean): String {
    return if (status) "Active" else "Inactive"
}


@Composable
fun StationDetailsScreen(
    viewModel: AdminViewModel,
    stationId: String,
    onBack: () -> Unit) {
    // In a real app, fetch details based on stationId
    //val station = sampleStationDetails

    LaunchedEffect(Unit) {
        viewModel.getStationDetails(stationId)
    }

    val station by viewModel.stationDetails.collectAsState()

    val state by viewModel.state.collectAsState()

    val measurements by viewModel.stationMeasurements.collectAsState()



    var showDialog by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(true) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var pendingStatus by remember { mutableStateOf<Boolean?>(null) }
    var showMeasurements by remember { mutableStateOf(false) }

    LaunchedEffect(station.status) {
        isActive = station.status
    }


    StatusDialog(
        showDialog = showDialog,
        currentStatus = isActive,
        onDismiss = { showDialog = false },
        onStatusSelected = { isActive = it },
        onStatusChanged = { selected ->
            pendingStatus = selected
            showConfirmDialog = true
        }
    )

    ConfirmStatusChangeDialog(
        show = showConfirmDialog,
        newStatus = pendingStatus ?: isActive,
        onConfirm = {
            val newStatus = pendingStatus ?: isActive
            viewModel.postStationStatus(stationId, newStatus)
            showConfirmDialog = false
            pendingStatus = null
        },
        onDismiss = {
            showConfirmDialog = false
            pendingStatus = null
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Button(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back to Stations")
            }
        }

        item {
            StationDetailsCard(station = station)
        }

        item {
            OutlinedButton(
                onClick = {
                    if (!showMeasurements && measurements.isEmpty()) {
                        viewModel.getStationMeasurements(stationId)
                    }
                    showMeasurements = !showMeasurements
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (showMeasurements) "Hide measurements" else "Show measurements")
            }
        }

        if (showMeasurements) {
            item {
                MeasurementsDetails(measurements)
            }
        }

        item {
            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Create, contentDescription = "Change Status")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Status")
            }
        }
    }
}

@Composable
fun StationDetailsCard(
    station: MonitoringStation,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔹 Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Monitoring station",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 🔹 Status badge
                StationStatusBadge(status = station.status)
            }

            Divider()

            // 🔹 Details
            DetailItem(label = "Station ID", value = station.id)
            DetailItem(label = "Type", value = station.type)
            DetailItem(label = "Latitude", value = station.latitude)
            DetailItem(label = "Longitude", value = station.longitude)
            DetailItem(label = "Created", value = station.created)
        }
    }
}

@Composable
private fun StationStatusBadge(status: Boolean) {
    val (backgroundColor, contentColor) = if (status) {
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }


    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Text(
            text = specifyStatus(status),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun MeasurementsDetails(
    measurements: List<Measurement>
){

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Measurements", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            for (measurement in measurements) {
                MeasurementItem(measurement = measurement)
            }
        }
    }

}

@Composable
fun MeasurementItem(
    measurement: Measurement,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // 🔹 Header
            Text(
                text = "Sensor ${measurement.sensorId}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // 🔹 Date & Time
            Text(
                text = "${measurement.date} • ${measurement.time}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            // 🔹 Measurements
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MeasurementValue(
                    label = "PM2.5",
                    value = measurement.PM25.toString(),
                    unit = measurement.unit
                )

                MeasurementValue(
                    label = "PM10",
                    value = measurement.PM10.toString(),
                    unit = measurement.unit
                )

                MeasurementValue(
                    label = "AQI",
                    value = measurement.AQI.toString(),
                    unit = ""
                )
            }
        }
    }
}

@Composable
private fun MeasurementValue(
    label: String,
    value: String,
    unit: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$value $unit",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}


/*
@Preview(showBackground = true)
@Composable
fun StationDetailsScreenPreview() {
    MaterialTheme {
        StationDetailsScreen(stationId = "station-1", onBack = {})
    }
}
*/