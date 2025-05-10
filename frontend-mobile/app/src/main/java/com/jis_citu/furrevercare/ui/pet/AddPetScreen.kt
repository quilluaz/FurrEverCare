@file:OptIn(ExperimentalMaterial3Api::class) // Apply to the whole file for TopAppBar etc.

package com.jis_citu.furrevercare.ui.pet

import android.net.Uri
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Category // Icon for Species
import androidx.compose.material.icons.filled.Pets // Icon for Breed
import androidx.compose.material.icons.filled.Wc // Icon for Gender
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
// import com.jis_citu.furrevercare.theme.PrimaryGreen // Use MaterialTheme.colorScheme.primary instead
import com.jis_citu.furrevercare.ui.pet.viewmodel.EditPetNavigationEvent
import com.jis_citu.furrevercare.ui.pet.viewmodel.EditPetUiState
import com.jis_citu.furrevercare.ui.pet.viewmodel.EditPetViewModel
import com.jis_citu.furrevercare.utils.decodeBase64 // Your utility function
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditPetScreen( // This screen handles both Add and Edit Pet
    navController: NavController,
    viewModel: EditPetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current // For ImageRequest

    // Handle navigation events from ViewModel
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is EditPetNavigationEvent.NavigateBack -> {
                    navController.popBackStack() // More robust than navigateUp in some cases
                }
            }
        }
    }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.updateImageUri(uri)
        }
    )

    EditPetScreenContent(
        uiState = uiState,
        onImagePickerLaunch = { imagePickerLauncher.launch("image/*") },
        onPetNameChange = viewModel::updatePetName,
        onSpeciesChange = viewModel::updateSpecies,
        onBreedChange = viewModel::updateBreed,
        onGenderChange = viewModel::updateGender,
        onAgeChange = viewModel::updateAge,
        onWeightChange = viewModel::updateWeight,
        onSaveClick = viewModel::savePet,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetScreenContent(
    uiState: EditPetUiState,
    onImagePickerLaunch: () -> Unit,
    onPetNameChange: (String) -> Unit,
    onSpeciesChange: (String) -> Unit,
    onBreedChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error message in Snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            // Optionally, clear the error in VM after showing if it's a one-time message
        }
    }
    // Show success message in Snackbar
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess && !uiState.isEditMode) { // Show only for "Add" success for now
            snackbarHostState.showSnackbar(
                message = "Pet added successfully!",
                duration = SnackbarDuration.Short
            )
        } else if (uiState.saveSuccess && uiState.isEditMode) {
            snackbarHostState.showSnackbar(
                message = "Pet updated successfully!",
                duration = SnackbarDuration.Short
            )
        }
    }


    FurrEverCareTheme { // Ensure theme is applied
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(if (uiState.isEditMode) "Edit Pet Profile" else "Add New Pet") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading && uiState.isEditMode) { // Show loader only when loading existing pet
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pet Image Picker/Display
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable(onClick = onImagePickerLaunch),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageData: Any? = uiState.imageUri
                            ?: uiState.existingImageBase64?.let { decodeBase64(it) }
                            ?: R.drawable.logo_icon_colored // Use your placeholder

                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(data = imageData)
                                    .placeholder(R.drawable.logo_icon_colored)
                                    .error(R.drawable.logo_icon_colored)
                                    .crossfade(true)
                                    .build()
                            ),
                            contentDescription = "Pet Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
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
                        onValueChange = onPetNameChange,
                        label = { Text("Pet Name*") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving,
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Species Dropdown
                    var speciesExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = speciesExpanded,
                        onExpandedChange = { speciesExpanded = !speciesExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.species,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Species*") },
                            leadingIcon = { Icon(Icons.Filled.Category, contentDescription = "Species") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            enabled = !uiState.isSaving,
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = speciesExpanded,
                            onDismissRequest = { speciesExpanded = false }
                        ) {
                            uiState.speciesList.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        onSpeciesChange(selectionOption)
                                        speciesExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Breed Dropdown
                    var breedExpanded by remember { mutableStateOf(false) }
                    val breedDropdownEnabled = uiState.species.isNotEmpty() && uiState.breedList.isNotEmpty() && !uiState.isSaving
                    ExposedDropdownMenuBox(
                        expanded = breedExpanded && breedDropdownEnabled,
                        onExpandedChange = { if (breedDropdownEnabled) breedExpanded = !breedExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.breed,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Breed") },
                            leadingIcon = { Icon(Icons.Filled.Pets, contentDescription = "Breed") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedExpanded && breedDropdownEnabled) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            enabled = breedDropdownEnabled,
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = breedExpanded && breedDropdownEnabled,
                            onDismissRequest = { breedExpanded = false }
                        ) {
                            uiState.breedList.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        onBreedChange(selectionOption)
                                        breedExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender Dropdown
                    var genderExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender*") },
                            leadingIcon = { Icon(Icons.Filled.Wc, contentDescription = "Gender") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            enabled = !uiState.isSaving,
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            uiState.genderList.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        onGenderChange(selectionOption)
                                        genderExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Age
                    OutlinedTextField(
                        value = uiState.age,
                        onValueChange = onAgeChange,
                        label = { Text("Age (years)*") },
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
                        onValueChange = onWeightChange,
                        label = { Text("Weight (kg)*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        enabled = !uiState.isSaving,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Error message display already handled by SnackbarHost

                    Spacer(modifier = Modifier.weight(1f).defaultMinSize(minHeight = 24.dp)) // Push button to bottom

                    Button(
                        onClick = onSaveClick,
                        enabled = !uiState.isSaving && !(uiState.isLoading && uiState.isEditMode),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text(if (uiState.isEditMode) "Update Pet" else "Add Pet")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
                }
            }
        }
    }
}