package com.jis_citu.furrevercare.ui.resource

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.model.resources.ResourceItem
import com.jis_citu.furrevercare.model.resources.ResourceType
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.resource.viewmodel.DeletionResult
import com.jis_citu.furrevercare.ui.resource.viewmodel.ResourceDetailsUiState
import com.jis_citu.furrevercare.ui.resource.viewmodel.ResourceDetailsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDetailsScreen(
    navController: NavController,
    viewModel: ResourceDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    // Handle deletion results
    LaunchedEffect(uiState.deletionResult) {
        when (val result = uiState.deletionResult) {
            is DeletionResult.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Resource deleted successfully!",
                        duration = SnackbarDuration.Short
                    )
                }
                // Brief delay before navigating back to allow Snackbar to be seen
                kotlinx.coroutines.delay(1500)
                navController.popBackStack() // Go back to the list screen
                viewModel.clearDeletionResult()
            }
            is DeletionResult.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = result.message,
                        duration = SnackbarDuration.Long
                    )
                }
                viewModel.clearDeletionResult()
            }
            null -> { /* No action */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(uiState.resourceItem?.name ?: "Resource Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    uiState.resourceItem?.let { resource -> // Show only if resource is loaded
                        IconButton(onClick = {
                            navController.navigate(
                                Routes.ADD_EDIT_RESOURCE + "?resourceId=${resource.id}"
                            )
                        }) {
                            Icon(Icons.Filled.Edit, "Edit resource")
                        }
                        IconButton(onClick = { showDeleteConfirmationDialog = true }) {
                            Icon(Icons.Filled.Delete, "Delete resource")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    ErrorState( // Re-use ErrorState from ResourceListScreen or define locally
                        message = uiState.errorMessage ?: "An error occurred.",
                        onRetry = viewModel::loadResourceDetails,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                uiState.resourceItem != null -> {
                    ResourceDetailsContent(
                        resource = uiState.resourceItem!!, // Safe due to check
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> { // Should not happen if resourceId was valid but item is null post-load
                    Text(
                        "Resource not found or could not be loaded.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Delete Resource") },
            text = { Text("Are you sure you want to delete '${uiState.resourceItem?.name}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteResource()
                        showDeleteConfirmationDialog = false
                    }
                ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ResourceDetailsContent(resource: ResourceItem, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailItem(icon = getIconForResourceType(resource.resourceTypeEnum), label = "Type", value = resource.resourceTypeEnum.displayName)

        resource.contact?.let { contact ->
            DetailItem(icon = Icons.Filled.Phone, label = "Phone", value = contact.phoneNumber, clickableValue = contact.phoneNumber?.isNotBlank() == true) {
                contact.phoneNumber?.let { dialPhoneNumber(context, it) }
            }
            DetailItem(icon = Icons.Filled.Email, label = "Email", value = contact.emailAddress, clickableValue = contact.emailAddress?.isNotBlank() == true) {
                contact.emailAddress?.let { sendEmail(context, it) }
            }
            DetailItem(icon = Icons.Filled.Language, label = "Website", value = contact.website, clickableValue = contact.website?.isNotBlank() == true) {
                contact.website?.let { openUrl(context, it) }
            }
            DetailItem(icon = Icons.Filled.LocationOn, label = "Address", value = contact.address, clickableValue = contact.address?.isNotBlank() == true) {
                contact.address?.let { openMap(context, it) }
            }
            // Add Latitude/Longitude display if available and needed
        }

        resource.operatingHours?.takeIf { it.isNotBlank() }?.let {
            DetailItem(icon = Icons.Filled.Schedule, label = "Operating Hours", value = it)
        }
        resource.notes?.takeIf { it.isNotBlank() }?.let {
            DetailItem(icon = Icons.Filled.Notes, label = "Notes", value = it)
        }
    }
}

@Composable
fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String?,
    clickableValue: Boolean = false,
    onValueClick: (() -> Unit)? = null
) {
    if (value.isNullOrBlank()) return

    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp).padding(end = 8.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = if (clickableValue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = if (clickableValue && onValueClick != null) Modifier.clickable { onValueClick() } else Modifier
            )
        }
    }
}

// Helper functions for intents (place outside composable or in a utility file)
fun dialPhoneNumber(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle case where no app can handle the intent
        Toast.makeText(context, "Cannot open dialer", Toast.LENGTH_SHORT).show()
    }
}

fun sendEmail(context: Context, emailAddress: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // Only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open email app", Toast.LENGTH_SHORT).show()
    }
}

fun openUrl(context: Context, url: String) {
    var completeUrl = url
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        completeUrl = "http://$url"
    }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(completeUrl))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open website", Toast.LENGTH_SHORT).show()
    }
}

fun openMap(context: Context, address: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(address)}"))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open map", Toast.LENGTH_SHORT).show()
    }
}