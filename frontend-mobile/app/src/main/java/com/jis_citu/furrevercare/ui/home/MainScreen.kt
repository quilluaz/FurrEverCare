package com.jis_citu.furrevercare.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.ui.tooling.preview.Preview // Added for Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Added for Preview
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
// Removed direct import of PrimaryGreen as we'll use MaterialTheme.colorScheme.primary
import com.jis_citu.furrevercare.ui.profile.ProfileScreen
import com.jis_citu.furrevercare.ui.pet.PetListScreen
import com.jis_citu.furrevercare.ui.resource.ResourceListScreen
// Make sure HomeScreen is imported if it's in a different sub-package of ui.home,
// or if MainScreen.kt is in a different package than HomeScreen.kt
// Assuming HomeScreen.kt is in the same package: com.jis_citu.furrevercare.ui.home

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun MainScreen(navController: NavController) {
    val bottomNavItems = listOf(
        BottomNavItem("Home", Routes.HOME, Icons.Default.Home),
        BottomNavItem("Pets", Routes.PET_LIST, Icons.Default.Pets),
        BottomNavItem("Resources", Routes.RESOURCE_LIST, Icons.Default.Folder),
        BottomNavItem("Profile", Routes.PROFILE, Icons.Default.Person)
    )

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface, // Or surfaceColorAtElevation for a slight tint
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            // NOTE: This current setup swaps composables directly
                            // based on selectedItemIndex. It does NOT use NavController
                            // to navigate between Home, Pets, Resources, Profile.
                            // If you intend to use true navigation for bottom tabs
                            // (e.g., for separate back stacks per tab), you'd use
                            // an inner NavHost here and navigate with navController.navigate(item.route).
                            // For now, this direct composable swapping is fine.
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
                            indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), // This is good
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
                0 -> HomeScreen(navController) // navController from AppNavGraph is passed
                1 -> PetListScreen(navController) // Pass the same navController
                2 -> ResourceListScreen(/* navController = navController */) // Pass navController if ResourceListScreen needs it
                3 -> ProfileScreen(navController)  // Pass the same navController
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    FurrEverCareTheme {
        MainScreen(rememberNavController())
    }
}