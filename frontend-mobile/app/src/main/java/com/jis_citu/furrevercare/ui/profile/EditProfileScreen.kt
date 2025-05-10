package com.jis_citu.furrevercare.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
// import androidx.compose.foundation.clickable // Only if image editing UI is interactive
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha // For disabled colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle // Placeholder for profile pic
// import androidx.compose.material.icons.filled.Edit // For small edit icon on pic (disabled)
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Prefer MaterialTheme.colorScheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.model.User // Your corrected User model
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
// Import ViewModel and related classes
import com.jis_citu.furrevercare.ui.profile.viewmodel.EditProfileNavigationEvent
import com.jis_citu.furrevercare.ui.profile.viewmodel.EditProfileUiState
import com.jis_citu.furrevercare.ui.profile.viewmodel.EditProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is EditProfileNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Profile updated successfully!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Error: $it",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    EditProfileScreenContent(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onEmailChange = viewModel::updateEmail,
        onPhoneChange = viewModel::updatePhone,
        onSaveClick = viewModel::saveProfileChanges,
        onNavigateBack = { navController.popBackStack() },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenContent(
    uiState: EditProfileUiState, // UiState now has name, email, phone directly
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scrollState = rememberScrollState()

    FurrEverCareTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profile") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                if (uiState.isLoading && uiState.name.isEmpty()) { // Show loader only on initial load when name is not yet populated
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Picture Placeholder (Image Upload Disabled)
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profile Picture Placeholder",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Name
                        OutlinedTextField(
                            value = uiState.name, // Use direct uiState.name
                            onValueChange = onNameChange,
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !uiState.isSaving,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Email (Often read-only if it's a login identifier)
                        OutlinedTextField(
                            value = uiState.email, // Use direct uiState.email
                            onValueChange = onEmailChange,
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !uiState.isSaving && false, // Keep email non-editable
                            readOnly = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = ContentAlpha.disabled),
                                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                                focusedBorderColor = MaterialTheme.colorScheme.primary, // Still define for consistency
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Phone
                        OutlinedTextField(
                            value = uiState.phone, // Use direct uiState.phone
                            onValueChange = onPhoneChange,
                            label = { Text("Phone (Optional)") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !uiState.isSaving,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f).defaultMinSize(minHeight = 32.dp))

                        Button(
                            onClick = onSaveClick,
                            enabled = !uiState.isSaving && !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 3.dp)
                            } else {
                                Text(
                                    text = "Save Changes",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Previews ---
@Preview(showBackground = true, name = "EditProfileScreen Light")
@Composable
fun EditProfileScreenPreviewLight() {
    FurrEverCareTheme(darkTheme = false) {
        EditProfileScreenContent(
            uiState = EditProfileUiState( // Use the simplified UiState with direct fields
                name = "John Doe",
                email = "john.doe@example.com",
                phone = "09123456789",
                isLoading = false,
                isSaving = false
            ),
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onSaveClick = {},
            onNavigateBack = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "EditProfileScreen Dark Saving")
@Composable
fun EditProfileScreenPreviewDarkSaving() {
    FurrEverCareTheme(darkTheme = true) {
        EditProfileScreenContent(
            uiState = EditProfileUiState( // Use the simplified UiState with direct fields
                name = "Jane Smith",
                email = "jane.smith@example.com",
                phone = "",
                isLoading = false,
                isSaving = true
            ),
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onSaveClick = {},
            onNavigateBack = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}