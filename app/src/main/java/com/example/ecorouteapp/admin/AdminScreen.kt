package com.example.ecorouteapp.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ecorouteapp.admin.stations.MonitoringStationsList
import com.example.ecorouteapp.admin.users.UsersScreen


data class UserRole(val name: String, val color: Color)

@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onStationClick: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.getMonitoringStations()
    }

    val monitoringStations by viewModel.monitoringStations.collectAsState()



    Column(modifier = Modifier.padding(16.dp)) {
        //AdminPanelHeader()
        //Spacer(modifier = Modifier.height(16.dp))
        AdminTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> MonitoringStationsList(monitoringStations, onStationClick)
            1 -> UsersScreen()
        }
    }
}
/*

@Composable
fun AdminPanelHeader() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Admin Panel", style = MaterialTheme.typography.headlineMedium)
            Text("Manage monitoring stations and user accounts", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}
*/

@Composable
fun AdminTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    TabRow(selectedTabIndex = selectedTab) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = { Text("Stations") },
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Stations") }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = { Text("Users") },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Users") }
        )
    }
}


/*

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    MaterialTheme {
        AdminScreen(onStationClick = {})
    }
}
*/
