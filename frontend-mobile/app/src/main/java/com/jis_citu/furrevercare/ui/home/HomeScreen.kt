@file:OptIn(ExperimentalMaterialApi::class)

package com.jis_citu.furrevercare.ui.home

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
// Ensure correct auto-mirrored icons are used if you updated them
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.model.ScheduledTask
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.home.viewmodel.DisplayReminder
import com.jis_citu.furrevercare.ui.home.viewmodel.HomeUiState // Make sure this is imported
import com.jis_citu.furrevercare.ui.home.viewmodel.HomeViewModel
import com.jis_citu.furrevercare.utils.decodeBase64 as utilDecodeBase64
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOverallLoading = uiState.isLoadingPets || uiState.isLoadingReminders
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isOverallLoading,
        onRefresh = viewModel::refreshData
    )

    HomeScreenContent(
        uiState = uiState,
        navController = navController,
        isOverallLoading = isOverallLoading,
        pullRefreshState = pullRefreshState
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState, // This now includes 'greeting'
    navController: NavController,
    isOverallLoading: Boolean,
    pullRefreshState: androidx.compose.material.pullrefresh.PullRefreshState
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(Modifier.pullRefresh(pullRefreshState)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    // *** CORRECTED CALL TO HomeHeader ***
                    HomeHeader(
                        navController = navController,
                        greeting = uiState.greeting, // Pass the greeting from uiState
                        userName = uiState.userName,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                uiState.errorMessage?.let { message ->
                    item {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    SectionTitleWithViewAll(
                        title = "Your Pets",
                        showViewAll = uiState.pets.isNotEmpty() || uiState.isLoadingPets,
                        onViewAllClicked = { navController.navigate(Routes.PET_LIST) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    HandlePetSection(
                        isLoading = uiState.isLoadingPets,
                        pets = uiState.pets,
                        navController = navController,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "Upcoming Tasks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                handleReminderSectionItems(
                    isLoading = uiState.isLoadingReminders,
                    reminders = uiState.reminders
                )

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            PullRefreshIndicator(
                refreshing = isOverallLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
private fun SectionTitleWithViewAll(
    title: String,
    showViewAll: Boolean,
    onViewAllClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (showViewAll) {
            TextButton(onClick = onViewAllClicked) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun HandlePetSection(
    isLoading: Boolean,
    pets: List<Pet>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    if (isLoading && pets.isEmpty()) {
        Box(
            modifier = modifier
                .height(130.dp)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (pets.isEmpty() && !isLoading) {
        Column(
            modifier = modifier
                .height(130.dp)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "No pets added yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate(Routes.ADD_PET) },
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Add Your First Pet")
            }
        }
    } else if (pets.isNotEmpty()) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(pets, key = { pet -> pet.petID }) { pet ->
                PetItem(pet = pet, navController = navController)
            }
        }
    }
}

private fun LazyListScope.handleReminderSectionItems(
    isLoading: Boolean,
    reminders: List<DisplayReminder>
) {
    if (isLoading && reminders.isEmpty()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    } else if (reminders.isEmpty() && !isLoading) {
        item {
            Text(
                "No upcoming tasks or reminders.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp, horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    } else if (reminders.isNotEmpty()) {
        items(reminders, key = { reminder -> reminder.id }) { displayReminder ->
            ReminderItem(
                reminder = displayReminder,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

// This is the HomeHeader that expects 'greeting' and 'userName'
@Composable
fun HomeHeader(
    navController: NavController,
    greeting: String, // Expects greeting
    userName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (userName.isNotBlank()) userName else "User",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(
            onClick = { navController.navigate(Routes.NOTIFICATIONS) },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PetItem(
    pet: Pet,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(90.dp)
            .clickable { navController.navigate("${Routes.PET_DETAILS}/${pet.petID}") }
            .padding(vertical = 8.dp)
    ) {
        val imageModifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)

        if (pet.imageBase64.isNullOrBlank()) {
            Image(
                imageVector = Icons.Filled.Pets,
                contentDescription = pet.name,
                modifier = imageModifier.padding(12.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(decodeBase64ToBitmap(pet.imageBase64))
                    .crossfade(true)
                    .error(R.drawable.cat) // Ensure this drawable exists
                    .placeholder(R.drawable.cat) // Ensure this drawable exists
                    .build(),
                contentDescription = pet.name,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = pet.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = pet.species,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

fun decodeBase64ToBitmap(base64String: String?): Bitmap? {
    if (base64String.isNullOrBlank()) return null
    return utilDecodeBase64(base64String)
}

@Composable
fun ReminderItem(
    reminder: DisplayReminder,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon: ImageVector = when (reminder.taskType) {
                ScheduledTask.TaskType.MEDICATION -> Icons.Filled.LocalHospital
                ScheduledTask.TaskType.VET_VISIT -> Icons.AutoMirrored.Filled.EventNote
                ScheduledTask.TaskType.APPOINTMENT -> Icons.AutoMirrored.Filled.EventNote
                ScheduledTask.TaskType.FEEDING -> Icons.Filled.Restaurant
                ScheduledTask.TaskType.WALK -> Icons.AutoMirrored.Filled.DirectionsWalk
                ScheduledTask.TaskType.GROOMING -> Icons.Filled.Shower
                ScheduledTask.TaskType.OTHER -> Icons.Filled.Notifications
                ScheduledTask.TaskType.UNKNOWN -> Icons.Filled.Notifications
            }
            Icon(
                imageVector = icon,
                contentDescription = reminder.taskType.name,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                reminder.petName?.let { petName ->
                    if (petName.isNotBlank()) {
                        Text(
                            text = "For: $petName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = reminder.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- Preview Data Setup ---
@RequiresApi(Build.VERSION_CODES.O)
fun getPreviewReminders(): List<DisplayReminder> {
    return listOf(
        DisplayReminder("1", "Luna", "Morning Meds for Luna", "08:00 AM", LocalDateTime.now().withHour(8).withMinute(0), ScheduledTask.TaskType.MEDICATION),
        DisplayReminder("2", "Max", "Vet Appointment", "10:30 AM - Tomorrow", LocalDateTime.now().plusDays(1).withHour(10).withMinute(30), ScheduledTask.TaskType.VET_VISIT),
        DisplayReminder("3", "Charlie", "Evening Walk", "07:00 PM", LocalDateTime.now().withHour(19).withMinute(0), ScheduledTask.TaskType.WALK)
    )
}

fun getPreviewPets(): List<Pet> {
    return listOf(
        Pet(petID = "p1", ownerID = "owner1", name = "Luna", species = "Dog", breed = "Golden Retriever", age = 2, gender = "Female", weight = 25.5, allergies = listOf("Peanuts"), imageBase64 = null),
        Pet(petID = "p2", ownerID = "owner1", name = "Max", species = "Cat", breed = "Siamese", age = 3, gender = "Male", weight = 5.2, allergies = null, imageBase64 = null),
        Pet(petID = "p3", ownerID = "owner1", name = "Charlie", species = "Dog", breed = "Beagle", age = 1, gender = "Male", weight = 12.0, allergies = listOf("Grass", "Pollen"), imageBase64 = null)
    )
}