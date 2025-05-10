package com.jis_citu.furrevercare.ui.pet

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote // For Schedules
import androidx.compose.material.icons.automirrored.filled.FactCheck // For Medical Records
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female // For Gender
import androidx.compose.material.icons.filled.Male // For Gender
import androidx.compose.material.icons.filled.MonitorHeart // For Emergency Profile
import androidx.compose.material.icons.filled.Pets // Placeholder if image is null
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
// Removed com.jis_citu.furrevercare.theme.PrimaryGreen as we'll use MaterialTheme.colorScheme
import com.jis_citu.furrevercare.ui.pet.viewmodel.PetDetailsViewModel
import com.jis_citu.furrevercare.ui.pet.viewmodel.PetDetailsUiState // For preview
import java.util.Locale // For capitalizing gender

// Sample data classes from your file (keep for now if used in VM or previews)
data class MedicalRecord(
    val id: String,
    val title: String,
    val date: String,
    val description: String
)

data class Schedule(
    val id: String,
    val title: String,
    val date: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsScreen(
    navController: NavController,
    viewModel: PetDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Sample data - TODO: Replace with actual data from ViewModel uiState.medicalRecords, uiState.schedules
    val medicalRecords = remember {
        listOf(
            MedicalRecord("1", "Annual Checkup", "Jan 15, 2023", "Regular annual checkup. All vitals normal."),
            MedicalRecord("2", "Vaccination", "Mar 10, 2023", "Rabies vaccination administered.")
        )
    }
    val schedules = remember {
        listOf(
            Schedule("1", "Grooming Appointment", "Apr 5, 2023", "10:00 AM"),
            Schedule("2", "Vet Checkup", "May 20, 2023", "2:30 PM")
        )
    }

    PetDetailsScreenContent(
        uiState = uiState,
        navController = navController,
        sampleMedicalRecords = medicalRecords, // Pass sample data for now
        sampleSchedules = schedules        // Pass sample data for now
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsScreenContent(
    uiState: PetDetailsUiState,
    navController: NavController,
    sampleMedicalRecords: List<MedicalRecord>, // Temporary for sample data
    sampleSchedules: List<Schedule>          // Temporary for sample data
) {
    val pet = uiState.pet
    val petImageBitmap = uiState.petImage

    FurrEverCareTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(pet?.name ?: "Pet Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        pet?.let { // Show edit button only if pet data is available
                            IconButton(onClick = { navController.navigate("${Routes.EDIT_PET}/${it.petID}") }) {
                                Icon(Icons.Filled.Edit, "Edit Pet")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Or primary
                        titleContentColor = MaterialTheme.colorScheme.onSurface, // Or onPrimary
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface, // Or onPrimary
                        actionIconContentColor = MaterialTheme.colorScheme.primary // Or onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (pet == null) {
                    Text(
                        text = "Pet not found.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { PetHeader(pet = pet, petImageBitmap = petImageBitmap) }

                        item { Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) }

                        item {
                            DetailActionCard(
                                title = "Emergency Profile",
                                icon = Icons.Filled.MonitorHeart,
                                onClick = { navController.navigate("${Routes.EMERGENCY_PROFILE}/${pet.petID}") }
                            )
                        }
                        item {
                            DetailActionCard(
                                title = "Medical Records",
                                icon = Icons.AutoMirrored.Filled.FactCheck,
                                onClick = { navController.navigate("${Routes.MEDICAL_RECORD_LIST}/${pet.petID}") } // TODO: Ensure this route exists and leads to actual screen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PetHeader(pet: Pet, petImageBitmap: Bitmap?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (petImageBitmap != null) {
                Image(
                    bitmap = petImageBitmap.asImageBitmap(),
                    contentDescription = pet.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Pets,
                    contentDescription = "Default Pet Image",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = pet.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "${pet.breed} (${pet.species})",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoChip(label = "Age", value = "${pet.age} yrs")
            InfoChip(label = "Weight", value = "${pet.weight} kg")
            InfoChip(
                label = "Gender",
                value = pet.gender.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                icon = if (pet.gender.equals("Male", ignoreCase = true)) Icons.Filled.Male else Icons.Filled.Female,
                iconTint = if (pet.gender.equals("Male", ignoreCase = true)) Color(0xFF4C89F0) else Color(0xFFF06292) // Example colors
            )
        }
        pet.allergies?.let { allergies ->
            if (allergies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Allergies: ${allergies.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String, icon: ImageVector? = null, iconTint: Color = MaterialTheme.colorScheme.primary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(2.dp))
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DetailActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp), // Increased vertical padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp) // Slightly larger icon
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium, // More prominent title
                    fontWeight = FontWeight.SemiBold, // Bolder
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) { // Re-defined here, or ensure it's in a common place
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium, // Adjusted for consistency
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp), // Consistent spacing
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun SimpleListItem(
    headlineContent: @Composable () -> Unit,
    supportingContent: (@Composable () -> Unit)? = null,
    overlineContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        overlineContent?.invoke()
        headlineContent()
        supportingContent?.invoke()
    }
}