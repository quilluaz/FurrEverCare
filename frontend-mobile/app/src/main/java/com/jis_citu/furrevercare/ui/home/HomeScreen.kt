package com.jis_citu.furrevercare.ui.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

data class Reminder(
    val id: String,
    val title: String,
    val time: String,
    val iconRes: Int
)

@Composable
fun HomeScreen(navController: NavController) {
    // Sample data
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

    val reminders = listOf(
        Reminder("1", "Vaccination", "Today, 2:00 PM", R.drawable.logo_icon_colored),
        Reminder("2", "Medication", "Tomorrow, 9:00 AM", R.drawable.logo_icon_colored)
    )

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
                    items(pets) { pet ->
                        PetItem(pet, navController)
                    }
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
                text = "John Doe",
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

@Composable
fun PetItem(pet: Pet, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clickable { navController.navigate("${Routes.PET_DETAILS}/${pet.id}") }
    ) {
        Image(
            painter = painterResource(id = pet.imageRes),
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

@Composable
fun ReminderItem(reminder: Reminder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FurrEverCareTheme {
        HomeScreen(rememberNavController())
    }
}
