package com.jis_citu.furrevercare.ui.pet

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Female // For gender icon
import androidx.compose.material.icons.filled.Male // For gender icon
import androidx.compose.material.icons.filled.Pets // For placeholder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
// Remove direct import of Background if using MaterialTheme.colorScheme.background
// import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.ui.pet.viewmodel.PetListViewModel
import com.jis_citu.furrevercare.ui.pet.viewmodel.PetListUiState
import com.jis_citu.furrevercare.utils.decodeBase64 // Your utility function

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    navController: NavController,
    viewModel: PetListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = viewModel::refreshPets // Call refreshPets from ViewModel
    )

    // React to isUserLoggedIn changes (e.g., navigate to login if necessary)
    // This is a basic example; a more robust solution might involve a shared AuthViewModel
    // or a higher-level check in your AppNavGraph or MainActivity.
    if (!uiState.isUserLoggedIn && uiState.errorMessage != null) {
        // Consider showing a dialog or navigating to login screen
        // For now, we'll let the error message display.
        Log.w("PetListScreen", "User not logged in, showing error from ViewModel.")
    }

    FurrEverCareTheme { // Ensure theme is applied, especially for previews
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Pets") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Or primary
                        titleContentColor = MaterialTheme.colorScheme.onSurface // Or onPrimary
                    )
                    // No navigation icon needed if this is a top-level destination in bottom nav
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD_PET) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Pet"
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues) // Apply padding from Scaffold
                    .pullRefresh(pullRefreshState)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp) // Horizontal padding for content
                ) {
                    // Search Bar (optional top padding if not covered by Scaffold padding)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search pets...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp), // Padding around search bar
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors( // Theme-aware colors
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    if (uiState.isLoading && uiState.pets.isEmpty()) { // Show loader only if list is empty
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.errorMessage != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = uiState.errorMessage ?: "An error occurred.",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        val filteredPets = remember(searchQuery, uiState.pets) {
                            if (searchQuery.isBlank()) {
                                uiState.pets
                            } else {
                                uiState.pets.filter { pet ->
                                    pet.name.contains(searchQuery, ignoreCase = true) ||
                                            pet.breed.contains(searchQuery, ignoreCase = true) ||
                                            pet.species.contains(searchQuery, ignoreCase = true)
                                }
                            }
                        }

                        if (filteredPets.isEmpty() && !uiState.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No pets match your search." else "No pets added yet. Tap the '+' button to add one!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(), // Takes remaining space
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                            ) {
                                items(filteredPets, key = { pet -> pet.petID }) { pet ->
                                    PetCard(pet = pet) {
                                        navController.navigate("${Routes.PET_DETAILS}/${pet.petID}")
                                    }
                                }
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
}

@Composable
fun PetCard(pet: Pet, onClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Themed color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageModifier = Modifier
                .size(72.dp) // Adjusted size
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer) // Placeholder bg

            if (pet.imageBase64.isNullOrBlank()) {
                Image(
                    imageVector = Icons.Filled.Pets, // Generic placeholder
                    contentDescription = pet.name,
                    modifier = imageModifier.padding(16.dp), // Padding for the icon itself
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(decodeBase64(pet.imageBase64)) // Use your utility function
                        .crossfade(true)
                        .error(R.drawable.cat) // Your error placeholder
                        .placeholder(R.drawable.cat) // Your loading placeholder
                        .build(),
                    contentDescription = "${pet.name} image",
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Themed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (pet.gender.equals("Male", ignoreCase = true)) Icons.Default.Male else Icons.Default.Female,
                        contentDescription = "Gender: ${pet.gender}",
                        tint = if (pet.gender.equals("Male", ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary, // Theme-aware gender colors
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${pet.breed.ifBlank { "N/A" }} • ${pet.age} ${if (pet.age == 1) "year" else "years"} old",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Themed
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Weight: ${pet.weight} kg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) // Themed
                )

                pet.allergies?.let { allergiesList ->
                    if (allergiesList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            // Using a more descriptive prefix
                            text = "Allergies: ${allergiesList.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error // Keep error color for allergies
                        )
                    }
                }
            }
        }
    }
}

// --- Previews ---
@Preview(showBackground = true, name = "PetListScreen Light")
@Composable
fun PetListScreenPreviewLight() {
    FurrEverCareTheme(darkTheme = false) {
        // For preview, you might need to mock the Hilt ViewModel or pass a dummy state
        // This preview will try to use the Hilt VM, which might not work well in pure @Composable previews
        // A better way is to have a PetListScreenContent composable that takes PetListUiState
        val samplePets = listOf(
            Pet(petID = "1", name = "Buddy", species = "Dog", breed = "Golden Retriever", age = 3, gender = "Male", weight = 30.5, imageBase64 = null, allergies = listOf("Chicken")),
            Pet(petID = "2", name = "Lucy", species = "Cat", breed = "Siamese", age = 2, gender = "Female", weight = 4.2, imageBase64 = null, allergies = null)
        )
        val previewUiState = PetListUiState(pets = samplePets, isLoading = false)
        // To fully preview, you'd ideally have a PetListScreenContent that accepts uiState
        PetListScreenContentForPreview(uiState = previewUiState, navController = rememberNavController(), onRefresh = {})
    }
}

@Preview(showBackground = true, name = "PetListScreen Dark")
@Composable
fun PetListScreenPreviewDark() {
    FurrEverCareTheme(darkTheme = true) {
        val samplePets = listOf(
            Pet(petID = "1", name = "Buddy", species = "Dog", breed = "Golden Retriever", age = 3, gender = "Male", weight = 30.5, imageBase64 = null, allergies = listOf("Chicken")),
            Pet(petID = "2", name = "Lucy", species = "Cat", breed = "Siamese", age = 2, gender = "Female", weight = 4.2, imageBase64 = null, allergies = null)
        )
        val previewUiState = PetListUiState(pets = samplePets, isLoading = false)
        PetListScreenContentForPreview(uiState = previewUiState, navController = rememberNavController(), onRefresh = {})
    }
}

@Preview(showBackground = true, name = "PetListScreen Empty")
@Composable
fun PetListScreenEmptyPreview() {
    FurrEverCareTheme {
        val previewUiState = PetListUiState(pets = emptyList(), isLoading = false)
        PetListScreenContentForPreview(uiState = previewUiState, navController = rememberNavController(), onRefresh = {})
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PetListScreenContentForPreview( // A stateless version for easier previewing
    uiState: PetListUiState,
    navController: NavController,
    onRefresh: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = onRefresh
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Pets") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ADD_PET) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, "Add Pet")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search pets...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                if (uiState.isLoading && uiState.pets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    val filteredPets = uiState.pets.filter { pet ->
                        pet.name.contains(searchQuery, ignoreCase = true) ||
                                pet.breed.contains(searchQuery, ignoreCase = true) ||
                                pet.species.contains(searchQuery, ignoreCase = true)
                    }
                    if (filteredPets.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(if (searchQuery.isNotBlank()) "No pets match your search." else "No pets added yet.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(filteredPets, key = { pet -> pet.petID }) { pet ->
                                PetCard(pet = pet) {
                                    navController.navigate("${Routes.PET_DETAILS}/${pet.petID}")
                                }
                            }
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