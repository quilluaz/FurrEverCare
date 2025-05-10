package com.jis_citu.furrevercare.ui.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.* // Import relevant icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen
import com.jis_citu.furrevercare.ui.pet.viewmodel.EmergencyProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyProfileScreen(
    navController: NavController,
    viewModel: EmergencyProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show snackbar on successful save
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("Emergency profile saved successfully!")
                // Optionally navigate back automatically after save:
                // navController.navigateUp()
            }
            // Reset flag in VM? Or let reload handle it.
        }
    }
    // Show snackbar for errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar("Error: $it")
            }
        }
    }


    FurrEverCareTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Emergency Profile") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Enter critical information accessible in emergencies.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Blood Type
                        OutlinedTextField(
                            value = uiState.bloodType,
                            onValueChange = viewModel::updateBloodType,
                            label = { Text("Blood Type (Optional)") },
                            leadingIcon = { Icon(Icons.Filled.Bloodtype, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSaving,
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(Modifier.height(16.dp))

                        // Chronic Conditions (Multi-line TextField)
                        OutlinedTextField(
                            value = uiState.chronicConditions,
                            onValueChange = viewModel::updateChronicConditions,
                            label = { Text("Chronic Conditions (Optional, one per line)") },
                            leadingIcon = { Icon(Icons.Filled.MonitorHeart, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp), // Allow multiple lines
                            enabled = !uiState.isSaving,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(Modifier.height(16.dp))

                        // Emergency Contact
                        OutlinedTextField(
                            value = uiState.emergencyContact,
                            onValueChange = viewModel::updateEmergencyContact,
                            label = { Text("Emergency Contact (Name & Phone)") },
                            leadingIcon = { Icon(Icons.Filled.ContactPhone, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSaving,
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(Modifier.height(16.dp))

                        // Special Instructions
                        OutlinedTextField(
                            value = uiState.specialInstructions,
                            onValueChange = viewModel::updateSpecialInstructions,
                            label = { Text("Special Instructions / Notes") },
                            leadingIcon = { Icon(Icons.Filled.Notes, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                            enabled = !uiState.isSaving,
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(Modifier.height(32.dp))

                        // Save Button
                        Button(
                            onClick = viewModel::saveProfile,
                            enabled = !uiState.isSaving && !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Text("Save Emergency Profile")
                            }
                        }
                        Spacer(Modifier.height(16.dp)) // Bottom padding
                    }
                }
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
private fun EmergencyProfileScreenPreview() {
    FurrEverCareTheme {
        // Previewing this screen directly is hard due to Hilt/SavedStateHandle.
        Text("Emergency Profile Screen Preview (Requires Hilt)")
    }
}