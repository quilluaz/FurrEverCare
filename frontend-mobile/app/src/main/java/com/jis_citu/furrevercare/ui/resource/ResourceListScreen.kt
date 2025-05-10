package com.jis_citu.furrevercare.ui.resource // Ensure this package matches your structure

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi // For PullRefresh
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business // Default icon for resources
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jis_citu.furrevercare.model.resources.ResourceItem
import com.jis_citu.furrevercare.model.resources.ResourceType // Your ResourceType enum
import com.jis_citu.furrevercare.navigation.Routes // Your NavController routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.resource.viewmodel.ResourceListViewModel

// Import icons for specific resource types if you want to customize them
import androidx.compose.material.icons.filled.Healing // Vet
import androidx.compose.material.icons.filled.Store // Pet Store
import androidx.compose.material.icons.filled.ContentCut // Groomer
import androidx.compose.material.icons.filled.HomeWork // Shelter / Pet Sitter / Boarding
import androidx.compose.material.icons.filled.School // Trainer
import androidx.compose.material.icons.filled.LocalHospital // Emergency Clinic / Hospital

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ResourceListScreen(
    navController: NavController,
    viewModel: ResourceListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = viewModel::loadResources // Call the loadResources function on refresh
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pet Resources") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, // Or surfaceContainerHighest
                    titleContentColor = MaterialTheme.colorScheme.onSurface // Or onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to AddEditResourceScreen.
                    // We'll define this route and its optional argument later.
                    navController.navigate(Routes.ADD_EDIT_RESOURCE)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "Add new resource")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            when {
                uiState.isLoading && uiState.resources.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    ErrorState(
                        message = uiState.errorMessage ?: "An unexpected error occurred.",
                        onRetry = viewModel::loadResources,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 16.dp)
                    )
                }
                uiState.resources.isEmpty() && !uiState.isLoading -> {
                    EmptyState(
                        message = "No pet resources found.\nTap the '+' button to add your first one!",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(all = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.resources, key = { resource -> resource.id }) { resource ->
                            ResourceCard(
                                resourceItem = resource,
                                onClick = {
                                    navController.navigate("${Routes.RESOURCE_DETAILS}/${resource.id}")
                                }
                            )
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ResourceCard(resourceItem: ResourceItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp), // Consistent with HomeScreen example if any
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) // or surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getIconForResourceType(resourceItem.resourceTypeEnum),
                contentDescription = resourceItem.resourceTypeEnum.displayName,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resourceItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = resourceItem.resourceTypeEnum.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Display address from ResourceContact if available
                resourceItem.contact.address?.let { address ->
                    if (address.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun getIconForResourceType(type: ResourceType): ImageVector {
    return when (type) {
        ResourceType.VET -> Icons.Filled.Healing
        ResourceType.GROOMER -> Icons.Filled.ContentCut
        ResourceType.PET_STORE -> Icons.Filled.Store
        ResourceType.SHELTER -> Icons.Filled.HomeWork // Or a more specific shelter icon
        ResourceType.TRAINER -> Icons.Filled.School
        ResourceType.PET_SITTER -> Icons.Filled.HomeWork // Or a specific pet sitting icon
        ResourceType.EMERGENCY_HOSPITAL -> Icons.Filled.LocalHospital
        ResourceType.OTHER -> Icons.Filled.Business // Default/Other
    }
}


@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Business, // You can choose a more indicative icon
            contentDescription = "No resources found",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium, // Slightly larger for emphasis
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = "Error",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}