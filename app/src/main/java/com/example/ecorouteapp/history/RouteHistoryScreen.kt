package com.example.ecorouteapp.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RouteHistoryScreen(
    viewModel: RouteViewModel,
    onRouteClick: (String) -> Unit) {
    /*val routeHistoryList = listOf(
        RouteHistory("1", "20.12.2025", "completed", "13:12", "25m", 5, "38.2 µg/m³", "52.1 µg/m³"),
        RouteHistory("2", "16.12.2025", "completed", "13:12", "45m", 9, "32.8 µg/m³", "48.5 µg/m³"),
        RouteHistory("3", "9.12.2025", "completed", "13:12", "1h 2m", 12, "51.3 µg/m³", "72.8 µg/m³")
    )*/

    val routeHistoryList by viewModel.historyUiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getHistory()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Route History", style = MaterialTheme.typography.headlineMedium)
        Text(text = "View your past routes and air quality exposure", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(routeHistoryList) { route ->
                RouteHistoryItem(route = route, onClick = { onRouteClick(route.id) })
            }
        }
    }
}

@Composable
fun RouteHistoryItem(route: RouteHistory, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Date")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = route.date, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = route.status,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = "Time")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = route.time, fontSize = 14.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "Duration")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = route.duration, fontSize = 14.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Points")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${route.points} points", fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Avg PM2.5: ${route.avgPm25}", fontSize = 14.sp)
            Text(text = "Max PM2.5: ${route.maxPm25}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Info, contentDescription = "Details")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Details")
            }
        }
    }
}
/*

@Preview(showBackground = true)
@Composable
fun RouteHistoryScreenPreview() {
    MaterialTheme {
        RouteHistoryScreen(onRouteClick = {})
    }
}
*/
