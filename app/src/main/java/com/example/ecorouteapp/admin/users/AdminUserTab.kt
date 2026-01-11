package com.example.ecorouteapp.admin.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ecorouteapp.admin.AdminUser
import com.example.ecorouteapp.admin.UserRole


@Composable
fun UsersScreen() {
    val users = listOf(
        AdminUser(
            "Demo User",
            "demo@example.com",
            "24.09.2025",
            "13.12.2025",
            listOf(UserRole("Admin", Color(0xFF6200EE))),
            listOf("Location", "Data Collection")
        ),
        AdminUser(
            "john_doe",
            "john.doe@example.com",
            "24.10.2025",
            "18.12.2025",
            listOf(UserRole("Sensitive User", Color.Gray)),
            listOf("Location", "Data Collection", "Marketing")
        ),
        AdminUser(
            "jane_smith",
            "jane.smith@example.com",
            "8.11.2025",
            "21.12.2025",
            emptyList(),
            listOf("Location")
        ),
        AdminUser(
            "blocked_user",
            "blocked@example.com",
            "23.11.2025",
            "22.12.2025",
            listOf(UserRole("Blocked", Color.Red)),
            emptyList(),
            isBlocked = true
        )
    )
    var searchQuery by remember { mutableStateOf("") }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = "User Accounts", style = MaterialTheme.typography.headlineSmall)
                Text(text = "Manage user accounts and permissions", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "Users count")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${users.size} users", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by username or email...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(users) { user ->
                UserItem(user = user)
            }
        }
    }
}

@Composable
fun UserItem(user: AdminUser) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = user.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))
                user.roles.forEach {
                    RoleChip(role = it)
                }
            }
            Text(text = user.email, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Created: ${user.created}", color = Color.Gray, fontSize = 12.sp)
                Text("Updated: ${user.updated}", color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                user.permissions.forEach {
                    PermissionChip(permission = it)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(if (user.isBlocked) Icons.Default.AccountCircle else Icons.Default.Lock, contentDescription = "Block/Unblock")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun RoleChip(role: UserRole) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = role.color,
        contentColor = Color.White
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (role.name == "Admin") {
                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = role.name, fontSize = 12.sp)
        }
    }
}

@Composable
fun PermissionChip(permission: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.LightGray.copy(alpha = 0.5f),
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = permission, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        }
    }
}

@Composable
fun StatusChip(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color,
        contentColor = Color.White
    ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
    }
}
