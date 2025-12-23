package com.example.ecorouteapp.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    var username by remember { mutableStateOf("Demo User") }
    var isSensitiveUser by remember { mutableStateOf(false) }
    var locationDataCollection by remember { mutableStateOf(true) }
    var airQualityDataCollection by remember { mutableStateOf(true) }
    var marketingCommunications by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            ProfileSettings(
                username = username,
                onUsernameChange = { username = it },
                isSensitiveUser = isSensitiveUser,
                onSensitiveUserChange = { isSensitiveUser = it }
            )
        }
        item {
            PrivacyAndGdprConsent(
                locationDataCollection = locationDataCollection,
                onLocationDataCollectionChange = { locationDataCollection = it },
                airQualityDataCollection = airQualityDataCollection,
                onAirQualityDataCollectionChange = { airQualityDataCollection = it },
                marketingCommunications = marketingCommunications,
                onMarketingCommunicationsChange = { marketingCommunications = it }
            )
        }
        item {
            Button(
                onClick = { /* TODO: Handle save changes */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Save Changes", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettings(
    username: String,
    onUsernameChange: (String) -> Unit,
    isSensitiveUser: Boolean,
    onSensitiveUserChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Profile Settings")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Profile Settings", style = MaterialTheme.typography.titleLarge)
            }
            Text("Manage your personal information", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = "demo@example.com",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            Text("Email cannot be changed", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSwitch(
                title = "Sensitive User Status",
                description = "Receive enhanced health alerts and stricter air quality thresholds",
                checked = isSensitiveUser,
                onCheckedChange = onSensitiveUserChange
            )
        }
    }
}

@Composable
fun PrivacyAndGdprConsent(
    locationDataCollection: Boolean,
    onLocationDataCollectionChange: (Boolean) -> Unit,
    airQualityDataCollection: Boolean,
    onAirQualityDataCollectionChange: (Boolean) -> Unit,
    marketingCommunications: Boolean,
    onMarketingCommunicationsChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = "Privacy & GDPR Consent")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Privacy & GDPR Consent", style = MaterialTheme.typography.titleLarge)
            }
            Text("Manage your data collection and privacy preferences", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSwitch(
                title = "Location Data Collection",
                description = "Allow collection of GPS location data for route tracking and air quality monitoring",
                checked = locationDataCollection,
                onCheckedChange = onLocationDataCollectionChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSwitch(
                title = "Air Quality Data Collection",
                description = "Store and process air quality measurements from your routes",
                checked = airQualityDataCollection,
                onCheckedChange = onAirQualityDataCollectionChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingsSwitch(
                title = "Marketing Communications",
                description = "Receive updates, tips, and notifications about air quality in your area",
                checked = marketingCommunications,
                onCheckedChange = onMarketingCommunicationsChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (!marketingCommunications) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Warning: You will not receive air quality alerts and updates without marketing consent.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Your Data Rights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• You can withdraw consent at any time")
                    Text("• You have the right to access all your personal data")
                    Text("• You can request deletion of your data")
                    Text("• Your data is stored securely and never sold to third parties")
                }
            }
        }
    }
}

@Composable
fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}
