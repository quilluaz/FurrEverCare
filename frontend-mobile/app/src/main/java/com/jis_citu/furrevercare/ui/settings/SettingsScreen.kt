package com.jis_citu.furrevercare.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout // Added for logout icon
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
// import androidx.compose.ui.platform.LocalContext // No longer needed directly for preferences
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
// import com.jis_citu.furrevercare.data.PreferenceManager // No longer directly used
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.settings.viewmodel.SettingsViewModel // Import ViewModel
import com.jis_citu.furrevercare.ui.settings.viewmodel.SettingsNavigationEvent // Import Nav Event
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel() // Inject ViewModel
) {
    // val context = LocalContext.current // Not needed for preference logic anymore
    val coroutineScope = rememberCoroutineScope() // Still useful for Snackbar

    // Collect states from ViewModel
    val isDarkMode by viewModel.isDarkTheme.collectAsState()
    val selectedUnit by viewModel.unitPreference.collectAsState()

    // Local UI states
    var notificationsEnabled by remember { mutableStateOf(true) } // Assuming this is local or comes from another source
    var showUnitDropdown by remember { mutableStateOf(false) }
    var deleteDialogOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

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


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                                    onCheckedChange = { newThemeState ->
                                        viewModel.setDarkTheme(newThemeState) // Use ViewModel
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
                                        text = "$selectedUnit ▾", // Use ViewModel state
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
                                                    viewModel.setUnitPreference(option) // Use ViewModel
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
                // Privacy Policy Item
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.Lock,
                            label = "Privacy Policy",
                            trailingContent = {
                                IconButton(onClick = {
                                    // TODO: Navigate to actual privacy policy screen if it exists
                                    // navController.navigate(Routes.PRIVACY_POLICY)
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Privacy Policy clicked") }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View Privacy Policy")
                                }
                            }
                        )
                    }
                }

                // Data Usage Item
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.Default.Storage,
                            label = "Data Usage",
                            trailingContent = {
                                IconButton(onClick = {
                                    // TODO: Navigate to actual data usage screen if it exists
                                    // navController.navigate(Routes.DATA_USAGE)
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Data Usage clicked") }
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View Data Usage")
                                }
                            }
                        )
                    }
                }


                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SettingsCard {
                        SettingsRow(
                            icon = Icons.AutoMirrored.Filled.Logout, // Changed icon for clarity
                            label = "Logout",
                            textColor = MaterialTheme.colorScheme.error, // Use theme error color
                            iconTint = MaterialTheme.colorScheme.error,  // Use theme error color
                            trailingContent = {
                                // Removed the "Delete" text, click directly on the row
                            },
                            onClick = { // Make the whole row clickable for logout
                                viewModel.logoutUser() // Call ViewModel to handle logout and navigation
                                // Snackbar can be shown from VM event or here if simpler
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Logged out successfully.")
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
                            textColor = MaterialTheme.colorScheme.error,
                            iconTint = MaterialTheme.colorScheme.error,
                            trailingContent = {
                                // Removed the "Delete" text, click directly on the row if preferred
                                // Or keep it as a more explicit action button
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Delete Account", tint = MaterialTheme.colorScheme.error)
                            },
                            onClick = { deleteDialogOpen = true } // Make the whole row clickable
                        )
                    }
                }


                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "FurrEverCare v1.1.4", // Example version
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth(), // Removed .align(Alignment.CenterHorizontally) as LazyColumn handles width
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center // Added for centering text
                    )
                    Spacer(modifier = Modifier.height(80.dp)) // For bottom padding
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
                        // TODO: Implement account deletion logic (call ViewModel method that interacts with backend and Firebase)
                        coroutineScope.launch { snackbarHostState.showSnackbar("Account deletion initiated (not implemented).") }
                    }) {
                        Text("Yes, Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteDialogOpen = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// SettingsCard and SettingsRow Composables (from your provided file, minor adjustments for consistency)
@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // Subtle elevation
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Slightly different from background
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) { // Adjusted padding
            content()
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    textColor: Color = LocalContentColor.current, // Use LocalContentColor
    iconTint: Color = LocalContentColor.current,  // Use LocalContentColor
    trailingContent: (@Composable () -> Unit)? = null, // Made optional
    onClick: (() -> Unit)? = null // Made onClick optional for rows that only display info
) {
    val rowModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp) // Increased padding for better touch target
    } else {
        modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    }
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = iconTint)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium, // Or bodyLarge
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        trailingContent?.invoke() // Invoke if not null
    }
}


@Preview(showBackground = true, name = "Settings Screen Light")
@Composable
fun SettingsScreenLightPreview() {
    FurrEverCareTheme(darkTheme = false) {
        Surface {
            SettingsScreen(navController = rememberNavController())
        }
    }
}

@Preview(showBackground = true, name = "Settings Screen Dark")
@Composable
fun SettingsScreenDarkPreview() {
    FurrEverCareTheme(darkTheme = true) {
        Surface {
            SettingsScreen(navController = rememberNavController())
        }
    }
}