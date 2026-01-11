package com.example.ecorouteapp.admin.stations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun StatusDialog(
    showDialog: Boolean,
    currentStatus: Boolean,
    onDismiss: () -> Unit,
    onStatusSelected: (Boolean) -> Unit,
    onStatusChanged: (Boolean) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Select status") },
            text = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            onStatusSelected(true)
                        }
                    ) {
                        RadioButton(
                            selected = currentStatus,
                            onClick = null
                        )
                        Text("Active")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            onStatusSelected(false)
                        }
                    ) {
                        RadioButton(
                            selected = !currentStatus,
                            onClick = null
                        )
                        Text("Inactive")
                    }
                }
            },
            confirmButton = {
                    Button(
                        onClick = {
                            onStatusChanged(currentStatus)
                            onDismiss()
                        },
                        content = {
                            Text("Save")
                        }
                    )
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDismiss()
                    },
                    content = {
                        Text("Cancel")
                    }
                )
            }
        )
    }
}


