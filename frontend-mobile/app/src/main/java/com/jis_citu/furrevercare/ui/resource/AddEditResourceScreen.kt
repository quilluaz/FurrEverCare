package com.jis_citu.furrevercare.ui.resource

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.model.resources.ResourceType
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.resource.viewmodel.AddEditResourceUiState
import com.jis_citu.furrevercare.ui.resource.viewmodel.AddEditResourceViewModel
import com.jis_citu.furrevercare.ui.resource.viewmodel.ResourceFormEvent
import com.jis_citu.furrevercare.ui.resource.viewmodel.SaveResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditResourceScreen(
    navController: NavController,
    viewModel: AddEditResourceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle save results
    LaunchedEffect(uiState.saveResult) {
        when (val result = uiState.saveResult) {
            is SaveResult.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = uiState.generalMessage ?: "Resource saved successfully!",
                        duration = SnackbarDuration.Short
                    )
                }
                // Brief delay before navigating back to allow Snackbar to be seen
                kotlinx.coroutines.delay(1500)
                navController.popBackStack()
                viewModel.clearSaveResult() // Clear after handling
            }
            is SaveResult.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = result.message,
                        duration = SnackbarDuration.Long
                    )
                }
                viewModel.clearSaveResult() // Clear after handling
            }
            null -> { /* No action needed */ }
        }
    }

    // Handle load errors
    LaunchedEffect(uiState.loadError) {
        uiState.loadError?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Long
                )
            }
            // Potentially pop back if load fails critically and it's an edit screen
            if (uiState.isEditing) { // If critical load error on edit, maybe navigate back
                // navController.popBackStack()
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Resource" else "Add New Resource") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.saveResource() },
                icon = { Icon(Icons.Filled.Save, contentDescription = "Save Resource") },
                text = { Text("Save") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                expanded = true // Or control with scroll state
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        if (uiState.isLoading && uiState.formState.name.isBlank() && uiState.isEditing) { // Show loading only when fetching existing data
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AddEditResourceForm(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                formState = uiState.formState,
                isSaving = uiState.isLoading && uiState.saveResult == null, // Only show saving indicator during save operation
                onEvent = viewModel::onFormEvent
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditResourceForm(
    modifier: Modifier = Modifier,
    formState: com.jis_citu.furrevercare.ui.resource.viewmodel.ResourceFormState, // Explicit type
    isSaving: Boolean,
    onEvent: (ResourceFormEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    val resourceTypes = remember { ResourceType.entries.toList() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = formState.name,
            onValueChange = { onEvent(ResourceFormEvent.NameChanged(it)) },
            label = { Text("Resource Name*") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            isError = formState.name.isBlank() && isSaving // Show error if saving and blank (example)
        )

        ExposedDropdownMenuBox(
            expanded = typeDropdownExpanded,
            onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = formState.selectedType.displayName,
                onValueChange = {}, // Not directly editable
                label = { Text("Resource Type*") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = typeDropdownExpanded,
                onDismissRequest = { typeDropdownExpanded = false }
            ) {
                resourceTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        onClick = {
                            onEvent(ResourceFormEvent.TypeChanged(type))
                            typeDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Optional: Show custom type name field if ResourceType.OTHER is selected
        // if (formState.selectedType == ResourceType.OTHER) {
        //     OutlinedTextField(
        //         value = formState.customTypeName,
        //         onValueChange = { onEvent(ResourceFormEvent.CustomTypeNameChanged(it)) },
        //         label = { Text("Custom Type Name (if Other)") },
        //         modifier = Modifier.fillMaxWidth(),
        //         singleLine = true,
        //         keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        //     )
        // }

        SectionTitle(title = "Contact Details")

        OutlinedTextField(
            value = formState.contactPhoneNumber,
            onValueChange = { onEvent(ResourceFormEvent.PhoneNumberChanged(it)) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            singleLine = true
        )

        OutlinedTextField(
            value = formState.contactEmail,
            onValueChange = { onEvent(ResourceFormEvent.EmailChanged(it)) },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            singleLine = true
        )

        OutlinedTextField(
            value = formState.contactWebsite,
            onValueChange = { onEvent(ResourceFormEvent.WebsiteChanged(it)) },
            label = { Text("Website") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next),
            singleLine = true
        )

        OutlinedTextField(
            value = formState.contactAddress,
            onValueChange = { onEvent(ResourceFormEvent.AddressChanged(it)) },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
            maxLines = 3
        )

        SectionTitle(title = "Additional Information")

        OutlinedTextField(
            value = formState.operatingHours,
            onValueChange = { onEvent(ResourceFormEvent.OperatingHoursChanged(it)) },
            label = { Text("Operating Hours (e.g., Mon-Fri 9am-5pm)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            maxLines = 2
        )

        OutlinedTextField(
            value = formState.notes,
            onValueChange = { onEvent(ResourceFormEvent.NotesChanged(it)) },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
            maxLines = 5
        )

        if (isSaving) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.height(80.dp)) // Space for the FAB
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 0.dp)
    )
}