package com.example.ecorouteapp.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecorouteapp.AppContainer
import com.example.ecorouteapp.AppViewModelFactory
import com.example.ecorouteapp.monitor.location.LocationRepository


val sampleRouteDetails = RouteDetails(
    id = "1",
    date = "20.12.2025",
    time = "13:12",
    status = "completed",
    duration = "25m",
    dataPoints = 5,
    avgPm25 = 38.2f,
    maxPm25 = 52.1f,
    avgPm10 = 58.7f,
    maxPm10 = 78.3f,
    exposureLevel = "High",
    exposureAssessment = "Your exposure was high. Consider choosing alternative routes with better air quality.",
    startTime = "20.12.2025, 13:12:53",
    endTime = "20.12.2025, 13:37:53",
    reportGeneratedTime = "20.12.2025, 13:37:53",
    healthRecommendations = listOf(
        "Consider using a mask (N95 or better) on similar routes",
        "Monitor air quality forecasts before planning outdoor activities",
        "Choose routes with more green spaces when possible"
    )
)

@Composable
fun RouteDetailsScreen(
    routeId: String,
    viewModel: RouteViewModel,
    onBack: () -> Unit)
{
    //val details = sampleRouteDetails

    val details by viewModel.detailsUiState.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.getRouteDetails(routeId)
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ActionToolbar(onBack)
        }
        item {
            RouteReportHeader(details)
        }
        item {
            SummaryGrid(details)
        }
        item {
            ExposureAssessmentCard(details)
        }
        item {
            AirQualityMeasurementsCard(details)
        }
        item {
            RouteTimelineCard(details)
        }
        item {
            HealthRecommendationsCard(details)
        }
    }
}

@Composable
fun ActionToolbar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Back")
        }
        Row {
            TextButton(onClick = { /* TODO: Export */ }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Export")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export")
            }
            TextButton(onClick = { /* TODO: Delete */ }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete")
            }
        }
    }
}

@Composable
fun RouteReportHeader(details: RouteDetails) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Route Report", style = MaterialTheme.typography.headlineMedium)
            Text("${details.date} at ${details.time}", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        }
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = details.status,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun SummaryGrid(details: RouteDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(icon = Icons.Default.CheckCircle, title = "Duration", value = details.duration, modifier = Modifier.weight(1f))
            SummaryCard(icon = Icons.Default.LocationOn, title = "Data Points", value = details.dataPoints.toString(), modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(icon = Icons.Default.Search, title = "Avg PM2.5", value = "${details.avgPm25}", unit = "µg/m³", modifier = Modifier.weight(1f))
            SummaryCard(icon = Icons.Default.Warning, title = "Max PM2.5", value = "${details.maxPm25}", unit = "µg/m³", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SummaryCard(icon: ImageVector, title: String, value: String, unit: String? = null, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                if (unit != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(unit, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
        }
    }
}


@Composable
fun ExposureAssessmentCard(details: RouteDetails) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Exposure Assessment", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Overall Exposure Level:", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = if (details.exposureLevel == "High") Color.Red else Color.Green,
                    shape = RoundedCornerShape(50),
                    contentColor = Color.White
                ) {
                    Text(
                        text = details.exposureLevel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(details.exposureAssessment, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
fun AirQualityMeasurementsCard(details: RouteDetails) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Air Quality Measurements", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("PM2.5 Particles", fontWeight = FontWeight.Bold)
            MeasurementRow("Average", "${details.avgPm25} µg/m³")
            MeasurementRow("Maximum", "${details.maxPm25} µg/m³")
            Spacer(modifier = Modifier.height(16.dp))
            Text("PM10 Particles", fontWeight = FontWeight.Bold)
            MeasurementRow("Average", "${details.avgPm10} µg/m³")
            MeasurementRow("Maximum", "${details.maxPm10} µg/m³")
        }
    }
}

@Composable
fun MeasurementRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RouteTimelineCard(details: RouteDetails) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Route Timeline", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            MeasurementRow("Started", details.startTime)
            MeasurementRow("Ended", details.endTime)
            MeasurementRow("Report Generated", details.reportGeneratedTime)
        }
    }
}

@Composable
fun HealthRecommendationsCard(details: RouteDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Health Recommendations", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            details.healthRecommendations.forEach { recommendation ->
                Text("• $recommendation", modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun RouteDetailsScreenPreview() {
    MaterialTheme {
        RouteDetailsScreen("1", onBack = {},
    }
}*/
