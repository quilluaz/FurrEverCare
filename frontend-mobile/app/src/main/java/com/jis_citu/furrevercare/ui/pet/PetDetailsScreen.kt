package com.jis_citu.furrevercare.ui.pet

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Import for LazyColumn items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos // <<< Moved Import
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices // Icon for Medical Records
import androidx.compose.material.icons.filled.Event // Icon for Schedules
import androidx.compose.material.icons.filled.Warning // Icon for Emergency Profile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Keep if needed, though not directly used here
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Keep for Preview
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.model.Pet // Import the actual Pet model
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen
import com.jis_citu.furrevercare.ui.pet.viewmodel.PetDetailsViewModel

// Sample data classes (if still needed for previews or initial setup)
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
    val pet = uiState.pet
    val petImageBitmap = uiState.petImage

    // Sample data - replace with actual data from ViewModel when ready
    // val medicalRecords = uiState.medicalRecords
    // val schedules = uiState.schedules
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

    FurrEverCareTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pet Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
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
                        text = uiState.errorMessage ?: "An unknown error occurred.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (pet == null) {
                    Text(
                        text = "Pet not found.",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Pet details loaded successfully
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Pet Info Card
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            PetInfoCard(pet = pet, petImageBitmap = petImageBitmap) { petId ->
                                navController.navigate("${Routes.EDIT_PET}/$petId")
                                Log.d("PetDetailsScreen", "Navigating to edit pet: $petId")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Emergency Profile Navigation Card
                        item {
                            ActionCard(
                                icon = Icons.Filled.Warning,
                                title = "Emergency Profile",
                                description = "View or update critical emergency information.",
                                onClick = { navController.navigate("${Routes.EMERGENCY_PROFILE}/${pet.petID}") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Action Buttons Row
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = { /* TODO: Navigate to Add Medical Record Screen */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Add Medical Record", textAlign = TextAlign.Center)
                                }

                                Button(
                                    onClick = { /* TODO: Navigate to Add Schedule Screen */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Add Schedule", textAlign = TextAlign.Center)
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Medical Records Section
                        item { SectionHeader(title = "Medical Records") }
                        if (medicalRecords.isEmpty()) {
                            item { EmptyStateText("No medical records added yet.") }
                        } else {
                            items(medicalRecords) { record -> // Use items extension function
                                MedicalRecordItem(record) // Call the single defined function
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Upcoming Schedules Section
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            SectionHeader(title = "Upcoming Schedules")
                        }
                        if (schedules.isEmpty()) {
                            item { EmptyStateText("No upcoming schedules.") }
                        } else {
                            items(schedules) { schedule -> // Use items extension function
                                ScheduleItem(schedule) // Call the single defined function
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        // Bottom padding
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

// --- Extracted Composables ---

@Composable
fun PetInfoCard(pet: Pet, petImageBitmap: Bitmap?, onEditClick: (String) -> Unit) { // Use actual Pet model
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(1.dp, PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (petImageBitmap != null) {
                        Image(
                            bitmap = petImageBitmap.asImageBitmap(),
                            contentDescription = pet.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else { // Placeholder if bitmap is null
                        Image(
                            painter = painterResource(id = R.drawable.logo_icon_colored), // Your placeholder
                            contentDescription = "Placeholder",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Details Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(pet.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("${pet.breed} (${pet.species})", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text("Age: ${pet.age}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Weight: ${pet.weight} kg", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        // Display Gender too
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Gender: ${pet.gender}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    pet.allergies?.let { allergies ->
                        if(allergies.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Allergies: ${allergies.joinToString()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Edit Button
                IconButton(
                    onClick = { onEditClick(pet.petID) },
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryGreen)
                ) {
                    Icon(Icons.Default.Edit, "Edit Pet", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ActionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Go", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun EmptyStateText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}


// --- Item Composables (KEEP ONLY ONE DEFINITION OF EACH) ---

@Composable
fun MedicalRecordItem(record: MedicalRecord) { // <<< Keep this one
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = record.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = record.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Duplicate MedicalRecordItem function was removed

@Composable
fun ScheduleItem(schedule: Schedule) { // <<< Keep this one
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                // Simple placeholder for date/month
                Text(
                    text = schedule.date.split(" ").firstOrNull()?.take(3)?.uppercase() ?: "N/A", // Attempt to get first 3 letters of month
                    style = MaterialTheme.typography.bodyLarge, // Adjusted style
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${schedule.date}, ${schedule.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
