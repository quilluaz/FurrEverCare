package com.jis_citu.furrevercare.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder // Resource Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.profile.ProfileScreen // Assuming this is your ProfileScreen composable
import com.jis_citu.furrevercare.ui.pet.PetListScreen    // Assuming this is your PetListScreen composable
import com.jis_citu.furrevercare.ui.resource.ResourceListScreen // Import ResourceListScreen


data class BottomNavItem(
    val name: String,
    val route: String, // This route is mainly for identification and potential future NavHost use
    val icon: ImageVector
)

@Composable
fun MainScreen(navController: NavController) { // This is the NavController from AppNavGraph
    val bottomNavItems = listOf(
        BottomNavItem("Home", Routes.HOME, Icons.Default.Home),
        BottomNavItem("Pets", Routes.PET_LIST, Icons.Default.Pets),
        BottomNavItem("Resources", Routes.RESOURCE_LIST, Icons.Default.Folder), // Using new route
        BottomNavItem("Profile", Routes.PROFILE, Icons.Default.Person)
    )

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
                            // This setup switches composables directly.
                            // It does not use the NavController for these main tab switches.
                            // If you wanted true navigation between bottom tabs (for backstacks per tab),
                            // you'd typically use an inner NavHost here.
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name
                            )
                        },
                        label = { Text(text = item.name) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItemIndex) {
                0 -> HomeScreen(navController) // Pass the main NavController
                1 -> PetListScreen(navController) // Pass the main NavController
                2 -> ResourceListScreen(navController) // Pass the main NavController
                3 -> ProfileScreen(navController)  // Pass the main NavController
            }
        }
    }
}