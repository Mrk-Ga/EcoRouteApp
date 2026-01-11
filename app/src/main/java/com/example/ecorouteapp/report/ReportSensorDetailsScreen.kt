package com.example.ecorouteapp.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])

@Composable
fun ReportSensorDetailsScreen(
    stationName: String,
    viewModel: ReportSensorViewModel,
    onBack: () -> Unit,
    onSave: ()-> Unit,
) {
/*    val sensors = listOf(
        SensorInfo("PM2.5", "45", "µg/m³", "12:38:18"),
        SensorInfo("PM10", "68", "µg/m³", "12:38:18"),
        SensorInfo("Temperature", "22", "°C", "12:38:18")
    )*/

    val station by viewModel.selectedStationUiState.collectAsState()

    val sensors = station.sensors

    val onSensorReportDataChange: (Int, String) -> Unit = { sensorId, report ->
        viewModel.updateReportData(sensorId, report)
    }

    LaunchedEffect(Unit) {
        viewModel.setSelectedStation(stationName)
    }



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        item {
            Button(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back to Stations")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Warsaw City Center",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Review sensor readings and report any inaccuracies",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(start = 32.dp) // align with title
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select an issue type for each sensor that appears to be malfunctioning. Only sensors with reported issues will be submitted.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(sensors.size) { index ->
            SensorReportItem(onDataChange = onSensorReportDataChange,sensor = sensors[index])
            if (index < sensors.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.postSensorReport()
                    onBack()
                          },
                modifier = Modifier.fillMaxWidth(),

            ) {
                Text("Save")
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorReportItem(onDataChange: (Int, String)->Unit, sensor: Sensor) {
    var expanded by remember { mutableStateOf(false) }
    val issueTypes = listOf("No issue", "Too high reading", "Too low reading", "Sensor not responding", "Erratic readings","Other issue")
    var selectedIssue by remember { mutableStateOf(issueTypes[0]) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = sensor.type, fontSize = 12.sp)
                }
                Text(text = "Last update: ${sensor.lastUpdate}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = sensor.value.toString(), style = MaterialTheme.typography.displaySmall)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = sensor.unit, modifier = Modifier.padding(bottom = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Report Issue", fontWeight = FontWeight.Bold)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedIssue,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    issueTypes.forEach { issue ->
                        DropdownMenuItem(
                            text = { Text(issue) },
                            onClick = {
                                selectedIssue = issue
                                expanded = false
                                onDataChange(sensor.sensorId, issue)
                            }
                        )
                    }
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun ReportSensorDetailsScreenPreview() {
    MaterialTheme {
        ReportSensorDetailsScreen(onBack = {}, onSave = {})
    }
}*/
