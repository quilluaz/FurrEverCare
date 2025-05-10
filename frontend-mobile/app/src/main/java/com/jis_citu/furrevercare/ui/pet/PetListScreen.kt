package com.jis_citu.furrevercare.ui.pet

import android.graphics.Bitmap // For Base64 result
import android.graphics.BitmapFactory // For Base64 decoding
import android.util.Base64 // For Base64 decoding
import android.util.Log // For logging
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Keep wildcard layout import
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // Keep wildcard material3 import
import androidx.compose.runtime.* // Keep wildcard runtime import
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap // For Base64 result
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Needed for Coil
import androidx.compose.ui.res.painterResource // For fallback image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage // <<< Import Coil AsyncImage
import coil.request.ImageRequest // <<< Import Coil ImageRequest
import com.jis_citu.furrevercare.utils.decodeBase64
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.model.Pet // Ensure Pet model is imported
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen

@Composable
fun PetListScreen(navController: NavController) {
    // TODO: Replace sample data with actual data fetched via ViewModel
    val pets = remember {
        listOf(
            // Sample data adjusted slightly - imageBase64 would normally come from backend
            Pet(
                petID = "1", // <<< Use petID
                name = "Max",
                species = "Dog",
                breed = "Golden Retriever",
                age = 3,
                gender = "Male",
                weight = 30.5,
                imageBase64 = null, // <<< Use imageBase64 (null for sample, relies on fallback)
                allergies = listOf("Chicken", "Peanuts")
            ),
            Pet(
                petID = "2", // <<< Use petID
                name = "Whiskers",
                species = "Cat",
                breed = "Siamese",
                age = 2,
                gender = "Female",
                weight = 4.2,
                imageBase64 = null, // <<< Use imageBase64 (null for sample, relies on fallback)
                allergies = listOf("Dairy")
            )
            // Add more sample pets if needed
        )
    }

    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background) // Assuming Background is defined in theme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "My Pets",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search
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
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp) // Consistent shape
            )

            // Pet list
            LazyColumn(
                modifier = Modifier.weight(1f), // Use weight to allow FAB space
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pets.filter {
                    // Filtering logic (remains the same)
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.breed.contains(searchQuery, ignoreCase = true) ||
                            it.species.contains(searchQuery, ignoreCase = true)
                }) { pet ->
                    // Pass correct petID to navigation
                    PetCard(pet) {
                        navController.navigate("${Routes.PET_DETAILS}/${pet.petID}") // <<< Use petID
                    }
                }

                // Add extra space at the bottom for FAB visibility when scrolling
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // FAB for adding a new pet
        FloatingActionButton(
            onClick = { navController.navigate(Routes.ADD_PET) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PrimaryGreen,
            contentColor = Color.White // Ensure icon color contrasts
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Pet"
                // tint is handled by contentColor
            )
        }
    }
}

@Composable
fun PetCard(pet: Pet, onClick: () -> Unit) {
    val context = LocalContext.current // Get context for Coil

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Set background color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pet image - Use Coil AsyncImage
            AsyncImage(
                model = ImageRequest.Builder(context)
                    // Try decoding Base64, fallback to placeholder
                    .data(pet.imageBase64?.let { decodeBase64(it) } ?: R.drawable.logo_icon_colored) // <<< Use placeholder
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.logo_icon_colored), // <<< Use placeholder
                error = painterResource(R.drawable.logo_icon_colored), // <<< Use placeholder
                contentDescription = "${pet.name} image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Pet info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Gender icon
                    Icon(
                        imageVector = if (pet.gender == "Male") Icons.Default.Male else Icons.Default.Female,
                        contentDescription = "Gender: ${pet.gender}",
                        tint = if (pet.gender == "Male") Color.Blue else Color.Red, // Consider theme colors
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${pet.breed} • ${pet.age} ${if (pet.age == 1) "year" else "years"} old",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Weight: ${pet.weight} kg",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Safely handle nullable allergies list
                pet.allergies?.let { allergiesList -> // <<< Use let for null check
                    if (allergiesList.isNotEmpty()) { // <<< Check non-null list
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row {
                            Text(
                                text = "Allergies: ",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = allergiesList.joinToString(", "), // <<< Use non-null list
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetListScreenPreview() {
    FurrEverCareTheme {
        PetListScreen(rememberNavController())
    }
}