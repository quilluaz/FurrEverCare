package com.jis_citu.furrevercare.ui.pet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.model.Pet
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen

@Composable
fun PetListScreen(navController: NavController) {
    val pets = remember {
        listOf(
            Pet(
                id = "1",
                name = "Max",
                species = "Dog",
                breed = "Golden Retriever",
                age = 3,
                gender = "Male",
                weight = 30.5,
                imageRes = R.drawable.dog,
                allergies = listOf("Chicken", "Peanuts")
            ),
            Pet(
                id = "2",
                name = "Whiskers",
                species = "Cat",
                breed = "Siamese",
                age = 2,
                gender = "Female",
                weight = 4.2,
                imageRes = R.drawable.cat,
                allergies = listOf("Dairy")
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
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
                    .padding(bottom = 16.dp)
            )

            // Pet list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pets.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.breed.contains(searchQuery, ignoreCase = true) ||
                            it.species.contains(searchQuery, ignoreCase = true)
                }) { pet ->
                    PetCard(pet) {
                        navController.navigate("${Routes.PET_DETAILS}/${pet.id}")
                    }
                }

                // Add extra space at the bottom for FAB
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
            containerColor = PrimaryGreen
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Pet",
                tint = Color.White
            )
        }
    }
}

@Composable
fun PetCard(pet: Pet, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pet image
            Image(
                painter = painterResource(id = pet.imageRes),
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
                        tint = if (pet.gender == "Male") Color.Blue else Color.Red,
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

                if (pet.allergies.isNotEmpty()) {
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
                            text = pet.allergies.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
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
