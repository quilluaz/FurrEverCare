package com.jis_citu.furrevercare.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos // Consistent forward arrow
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Notes // For Data Usage
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gavel // For Privacy Policy
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Straighten // Unit Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.settings.viewmodel.SettingsNavigationEvent
import com.jis_citu.furrevercare.ui.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect states from ViewModel
    val isDarkMode by viewModel.isDarkTheme.collectAsState()
    val selectedUnit by viewModel.unitPreference.collectAsState()

    // Local UI states (Notifications still local as per current ViewModel)
    var notificationsEnabled by remember { mutableStateOf(true) } // TODO: Persist if needed
    var showUnitDropdown by remember { mutableStateOf(false) }
    var deleteDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is SettingsNavigationEvent.Navigate -> {
                    navController.navigate(event.route) {
                        event.popUpToRoute?.let { popUpToRoute ->
                            popUpTo(popUpToRoute) { inclusive = event.inclusive }
                        }
                        launchSingleTop = event.launchSingleTop
                    }
                }
            }
        }
    }

    FurrEverCareTheme { // Ensures previews and screen use the correct theme
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface // Use theme color
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Use theme color
                        titleContentColor = MaterialTheme.colorScheme.onSurface, // Use theme color
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) // Use theme background
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    SectionTitle("General") // Optional section title
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.DarkMode,
                            label = "Dark Mode",
                            trailingContent = {
                                Switch(
                                    checked = isDarkMode, // From ViewModel
                                    onCheckedChange = { newThemeState ->
                                        viewModel.setDarkTheme(newThemeState)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.Notifications,
                            label = "Notifications", // TODO: Connect to actual notification preference
                            trailingContent = {
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it }
                                    // TODO: Add ViewModel call if this needs to be persisted
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Outlined.Straighten,
                            label = "Units",
                            trailingContent = {
                                Box {
                                    Row(
                                        modifier = Modifier.clickable { showUnitDropdown = true },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedUnit, // From ViewModel
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary // Highlight selected unit
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "Change unit",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showUnitDropdown,
                                        onDismissRequest = { showUnitDropdown = false }
                                    ) {
                                        listOf("Metric", "Imperial").forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    viewModel.setUnitPreference(option)
                                                    showUnitDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp)) // More space before next section
                }


                item {
                    SectionTitle("Legal & Information")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Filled.Gavel, // More relevant icon
                            label = "Privacy Policy",
                            onClick = { viewModel.onPrivacyPolicyClicked() }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.AutoMirrored.Filled.Notes, // More relevant icon
                            label = "Data Usage",
                            onClick = { viewModel.onDataUsageClicked() }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    SectionTitle("Account Actions")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.AutoMirrored.Filled.Logout,
                            label = "Logout",
                            textColor = MaterialTheme.colorScheme.error,
                            iconTint = MaterialTheme.colorScheme.error,
                            onClick = {
                                viewModel.logoutUser() // Handles navigation via event
                                // Snackbar can be shown by observing a state from VM after logout
                                // or just assuming navigation means success.
                                coroutineScope.launch {
                                    // snackbarHostState.showSnackbar("Logging out...") // VM will navigate
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.Delete,
                            label = "Delete Account",
                            textColor = MaterialTheme.colorScheme.error,
                            iconTint = MaterialTheme.colorScheme.error,
                            onClick = { deleteDialogOpen = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    Text(
                        text = "FurrEverCare v1.0.0", // Keep your app version
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
                }
            }
        }

        if (deleteDialogOpen) {
            AlertDialog(
                onDismissRequest = { deleteDialogOpen = false },
                icon = { Icon(Icons.Default.Delete, contentDescription = "Delete Icon", tint = MaterialTheme.colorScheme.error) },
                title = { Text("Delete Account", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone and all your data will be lost.") },
                confirmButton = {
                    Button(
                        onClick = {
                            deleteDialogOpen = false
                            // TODO: viewModel.deleteAccount() - Implement this in ViewModel
                            coroutineScope.launch { snackbarHostState.showSnackbar("Account deletion requested (not implemented).") }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.onError)
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
        shape = RoundedCornerShape(12.dp), // Consistent shape
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) // Slightly different from background
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) { // Reduced vertical padding for rows
            content()
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant, // Default to onSurfaceVariant
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,  // Default to onSurfaceVariant
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = modifier
        .fillMaxWidth()
        .let { if (onClick != null) it.clickable(onClick = onClick) else it } // Apply clickable only if onClick is provided
        .padding(horizontal = 16.dp, vertical = 16.dp) // Consistent padding within the row

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge, // Consistent typography
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        trailingContent?.invoke()
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary, // Use primary color for section titles
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
    )
}


// --- Previews ---
@Preview(showBackground = true, name = "Settings Screen Light")
@Composable
fun SettingsScreenLightPreview() {
    FurrEverCareTheme(darkTheme = false) {
        // For preview, you need a way to provide a SettingsViewModel instance
        // or create a stateless SettingsScreenContent composable.
        // This will likely use a default/mocked ViewModel if Hilt isn't fully active in preview.
        SettingsScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Settings Screen Dark")
@Composable
fun SettingsScreenDarkPreview() {
    FurrEverCareTheme(darkTheme = true) {
        SettingsScreen(navController = rememberNavController())
    }
}