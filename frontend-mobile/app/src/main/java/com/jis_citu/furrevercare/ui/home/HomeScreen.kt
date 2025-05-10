package com.jis_citu.furrevercare.ui.home

import android.graphics.BitmapFactory // Import for Base64 decoding
import android.util.Base64 // Import for Base64 decoding
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap // Import for Bitmap conversion
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Needed for Coil
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage // Use Coil's AsyncImage
import coil.request.ImageRequest
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.model.Pet // Make sure Pet model is imported
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen

data class Reminder(
    val id: String,
    val title: String,
    val time: String,
    val iconRes: Int
)

// Temporary data class for Sample Pet data using imageRes
// In a real app, you'd likely fetch Pet data directly
data class SamplePet(
    val id: String,
    val name: String,
    val species: String,
    val breed: String,
    val age: Int,
    val gender: String,
    val weight: Double,
    val imageRes: Int, // Using drawable resource for sample
    val allergies: List<String>
)


@Composable
fun HomeScreen(navController: NavController) {
    // Sample data using SamplePet for local drawable resources
    val samplePets = remember {
        listOf(
            SamplePet( // Use SamplePet here
                id = "1", // Use simple id for sample
                name = "Max",
                species = "Dog",
                breed = "Golden Retriever",
                age = 3,
                gender = "Male",
                weight = 30.5,
                imageRes = R.drawable.dog, // Keep using drawable for sample
                allergies = listOf("Chicken", "Peanuts")
            ),
            SamplePet( // Use SamplePet here
                id = "2",
                name = "Whiskers",
                species = "Cat",
                breed = "Siamese",
                age = 2,
                gender = "Female",
                weight = 4.2,
                imageRes = R.drawable.cat, // Keep using drawable for sample
                allergies = listOf("Dairy")
            )
        )
    }

    val reminders = listOf(
        Reminder("1", "Vaccination", "Today, 2:00 PM", R.drawable.logo_icon_colored),
        Reminder("2", "Medication", "Tomorrow, 9:00 AM", R.drawable.logo_icon_colored)
    )

    // --- Rest of your HomeScreen composable ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                HomeHeader(navController)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Pets",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.PET_LIST)
                        }
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Use PetItemForSample for the sample data
                    items(samplePets) { samplePet ->
                        PetItemForSample(samplePet, navController)
                    }
                    // NOTE: When you load actual 'Pet' data (with petID and imageBase64)
                    // you would use a different items block and call PetItem(pet, navController)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Upcoming Reminders",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(reminders) { reminder ->
                ReminderItem(reminder)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// --- HomeHeader remains the same ---
@Composable
fun HomeHeader(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good Morning,",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "John Doe", // Replace with actual user name if available
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(
            onClick = { navController.navigate(Routes.NOTIFICATIONS) },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications"
            )
        }
    }
}

// Updated PetItem to use actual Pet model data (petID, imageBase64)
// You would use this when displaying real data fetched from backend/database
@Composable
fun PetItem(pet: Pet, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            // Use pet.petID for navigation
            .clickable { navController.navigate("${Routes.PET_DETAILS}/${pet.petID}") }
    ) {
        // Use Coil's AsyncImage to load from Base64 or fallback
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                // Attempt to decode Base64 string. Provide a fallback drawable if it fails or is null.
                .data(pet.imageBase64?.let { decodeBase64(it) } ?: R.drawable.cat) // Replace R.drawable.ic_default_pet with your placeholder
                .crossfade(true)
                .build(),
            contentDescription = pet.name,
            placeholder = painterResource(R.drawable.cat), // Placeholder while loading
            error = painterResource(R.drawable.cat), // Fallback/Error image
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pet.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = pet.species,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

// Helper function to decode Base64 String to Bitmap
// Returns null if decoding fails
fun decodeBase64(base64String: String): android.graphics.Bitmap? {
    return try {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: IllegalArgumentException) {
        // Handle potential Base64 decoding errors
        null
    }
}


// New composable specifically for displaying SamplePet data using imageRes
// This keeps your preview and sample data working easily
@Composable
fun PetItemForSample(pet: SamplePet, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            // Use sample pet's id for navigation in preview/sample context
            .clickable { navController.navigate("${Routes.PET_DETAILS}/${pet.id}") }
    ) {
        Image(
            painter = painterResource(id = pet.imageRes), // Use imageRes directly
            contentDescription = pet.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pet.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = pet.species,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}


// --- ReminderItem remains the same ---
@Composable
fun ReminderItem(reminder: Reminder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Use CardDefaults.cardElevation
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Set background color explicitly if needed
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = reminder.iconRes),
                contentDescription = reminder.title,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = reminder.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
