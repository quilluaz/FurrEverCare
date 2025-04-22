package com.jis_citu.furrevercare.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.data.PreferenceManager
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isDarkStored by PreferenceManager.isDarkTheme(context).collectAsState(initial = false)
    var isDarkMode by remember { mutableStateOf(isDarkStored) }

    val unitStored by PreferenceManager.getUnitPreference(context).collectAsState(initial = "Metric")
    var selectedUnit by remember { mutableStateOf(unitStored) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var showUnitDropdown by remember { mutableStateOf(false) }
    var deleteDialogOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                item {
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.DarkMode,
                            label = "Dark Mode",
                            trailingContent = {
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = {
                                        isDarkMode = it
                                        coroutineScope.launch {
                                            PreferenceManager.setDarkTheme(context, it)
                                        }
                                    }
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.Notifications,
                            label = "Notifications",
                            trailingContent = {
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it }
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Outlined.Straighten,
                            label = "Units",
                            trailingContent = {
                                Box {
                                    Text(
                                        text = "$selectedUnit ▾",
                                        modifier = Modifier
                                            .clickable { showUnitDropdown = true }
                                            .padding(8.dp)
                                    )
                                    DropdownMenu(
                                        expanded = showUnitDropdown,
                                        onDismissRequest = { showUnitDropdown = false }
                                    ) {
                                        listOf("Metric", "Imperial").forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    selectedUnit = option
                                                    coroutineScope.launch {
                                                        PreferenceManager.setUnitPreference(context, option)
                                                    }
                                                    showUnitDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.Lock,
                            label = "Privacy Policy",
                            trailingContent = {
                                IconButton(onClick = { navController.navigate(Routes.PRIVACY_POLICY) }) {
                                    Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.Storage,
                            label = "Data Usage",
                            trailingContent = {
                                IconButton(onClick = { navController.navigate(Routes.DATA_USAGE) }) {
                                    Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = null)
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.Delete,
                            label = "Delete Account",
                            textColor = Color.Red,
                            iconTint = Color.Red,
                            trailingContent = {
                                Text(
                                    text = "Delete",
                                    color = Color.Red,
                                    modifier = Modifier
                                        .clickable { deleteDialogOpen = true }
                                        .padding(8.dp)
                                )
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "FurrEverCare v1.1.4",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        if (deleteDialogOpen) {
            AlertDialog(
                onDismissRequest = { deleteDialogOpen = false },
                title = { Text("Delete Account") },
                text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        deleteDialogOpen = false
                        // TODO: Implement Firebase Auth deletion logic here
                    }) {
                        Text("Yes, Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteDialogOpen = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    label: String,
    trailingContent: @Composable () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = iconTint)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        trailingContent()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    FurrEverCareTheme {
        SettingsScreen(rememberNavController())
    }
}
