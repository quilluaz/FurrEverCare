package com.jis_citu.furrevercare.ui.pet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto // Changed icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color // <<< Added Import
import androidx.compose.ui.graphics.asImageBitmap // <<< Added Import for Base64
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Import painterResource for fallback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType // Import KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview // Keep preview if needed
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Keep for preview if needed
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jis_citu.furrevercare.R // For placeholder image if needed
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen
// Import the ViewModel and related classes used in EditPetScreen
import com.jis_citu.furrevercare.utils.decodeBase64 // <<< Add this import
import com.jis_citu.furrevercare.ui.pet.viewmodel.EditPetViewModel
import com.jis_citu.furrevercare.ui.pet.viewmodel.EditPetNavigationEvent
// Removed viewModel.decodeBase64 as it's included below now
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetScreen( // Renamed composable for clarity if it edits pets
    navController: NavController,
    viewModel: EditPetViewModel = hiltViewModel() // Inject ViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Handle navigation
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is EditPetNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }
    }

    // Image Picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.updateImageUri(uri)
        }
    )

    FurrEverCareTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Pet Profile") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background, // Match background
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp) // Padding for content
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pet Image Picker/Display
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder background
                            .border(2.dp, PrimaryGreen, CircleShape)
                            .clickable { imagePicker.launch("image/*") }, // Launch picker on click
                        contentAlignment = Alignment.Center
                    ) {
                        // Determine the data source for the image
                        val imageData: Any? = if (uiState.imageUri != null) {
                            // Prioritize newly picked image Uri
                            uiState.imageUri
                        } else if (!uiState.existingImageBase64.isNullOrBlank()) {
                            // Use existing Base64 if available (decode it)
                            decodeBase64(uiState.existingImageBase64!!) // Use helper function
                        } else {
                            // Fallback to placeholder resource ID
                            R.drawable.logo_icon_colored // <<< Use your actual placeholder drawable
                        }

                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(data = imageData) // Load Uri, Bitmap, or Resource ID
                                    .placeholder(R.drawable.logo_icon_colored) // Placeholder while loading
                                    .error(R.drawable.logo_icon_colored) // Error fallback
                                    .build()
                            ),
                            contentDescription = "Pet Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop // Crop to fit the circle
                        )

                        // Icon overlay - Uses imported Color now
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Change Photo",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Pet Name
                    OutlinedTextField(
                        value = uiState.petName,
                        onValueChange = viewModel::updatePetName,
                        label = { Text("Pet Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving,
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- TODO: Implement ExposedDropdownMenuBoxes properly ---
                    // These require state variables for 'expanded' status and logic
                    // to populate the dropdown items and handle selections, likely
                    // involving updates to the ViewModel. The current setup is read-only.

                    // Species (Read-Only Example - Needs proper dropdown)
                    OutlinedTextField(
                        value = uiState.species,
                        onValueChange = {},
                        label = { Text("Species") },
                        readOnly = true,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Breed (Read-Only Example - Needs proper dropdown)
                    OutlinedTextField(
                        value = uiState.breed,
                        onValueChange = {},
                        label = { Text("Breed") },
                        readOnly = true,
                        enabled = uiState.species.isNotEmpty() && !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender (Read-Only Example - Needs proper dropdown)
                    OutlinedTextField(
                        value = uiState.gender,
                        onValueChange = {},
                        label = { Text("Gender") },
                        readOnly = true,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // --- End of Dropdown TODO ---


                    // Age
                    OutlinedTextField(
                        value = uiState.age,
                        onValueChange = viewModel::updateAge,
                        label = { Text("Age (years)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !uiState.isSaving,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Weight
                    OutlinedTextField(
                        value = uiState.weight,
                        onValueChange = viewModel::updateWeight,
                        label = { Text("Weight (kg)") }, // TODO: Add unit preference if needed
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !uiState.isSaving,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Display Error Message
                    uiState.errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = viewModel::savePetChanges,
                        enabled = !uiState.isSaving && !uiState.isLoading, // Enable only when not saving/loading
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Update Pet")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
                }
            }
        }
    }
}
