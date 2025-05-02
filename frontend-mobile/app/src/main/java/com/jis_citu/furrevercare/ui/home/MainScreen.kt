package com.jis_citu.furrevercare.ui.home

// Import necessary components
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder // Using Folder for Resources
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
// Removed Preview import as it's not used here directly
import androidx.compose.ui.unit.dp // Import dp
import androidx.navigation.NavController
// Removed rememberNavController import if MainScreen doesn't create its own controller
import com.jis_citu.furrevercare.navigation.Routes
// Removed AccentGold import if not used
import com.jis_citu.furrevercare.theme.FurrEverCareTheme // Keep if previewing
import com.jis_citu.furrevercare.theme.PrimaryGreen
// Import the screens that will be shown directly
import com.jis_citu.furrevercare.ui.profile.ProfileScreen
import com.jis_citu.furrevercare.ui.pet.PetListScreen
import com.jis_citu.furrevercare.ui.resource.ResourceListScreen // Import ResourceListScreen

data class BottomNavItem(
    val name: String,
    val route: String, // Use route constants from Routes object
    val icon: ImageVector
)

@Composable
fun MainScreen(navController: NavController) {
    // Define navigation items
    val bottomNavItems = listOf(
        BottomNavItem("Home", Routes.HOME, Icons.Default.Home),
        BottomNavItem("Pets", Routes.PET_LIST, Icons.Default.Pets),
        BottomNavItem("Resources", Routes.RESOURCE_LIST, Icons.Default.Folder), // Added Resources
        BottomNavItem("Profile", Routes.PROFILE, Icons.Default.Person)
    )

    // Use rememberSaveable to preserve selected index
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            // Optional: Navigate using NavController if structure requires it,
                            // but current setup swaps composables directly based on index.
                            // If you want true navigation between these bottom bar items:
                            // navController.navigate(item.route) {
                            //     popUpTo(navController.graph.startDestinationId)
                            //     launchSingleTop = true
                            // }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name
                            )
                        },
                        label = { Text(text = item.name) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            // FIX: Replace Elevation.Level1 with a Dp value
                            indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), // e.g., 3.dp
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Show the appropriate screen based on the selected index
            when (selectedItemIndex) {
                0 -> HomeScreen(navController)
                1 -> PetListScreen(navController)
                2 -> ResourceListScreen(/* navController */) // Pass navController ONLY IF ResourceListScreen needs it
                3 -> ProfileScreen(navController)
            }
        }
    }
}

// Optional: Add a Preview if needed
// @Preview(showBackground = true)
// @Composable
// fun MainScreenPreview() {
//     FurrEverCareTheme {
//         // You'd need a fake NavController for preview
//         MainScreen(rememberNavController())
//     }
// }