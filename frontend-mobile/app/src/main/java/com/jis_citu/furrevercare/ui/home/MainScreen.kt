package com.jis_citu.furrevercare.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.AccentGold
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.ui.article.SearchScreen
import com.jis_citu.furrevercare.ui.profile.ProfileScreen
import com.jis_citu.furrevercare.ui.pet.PetListScreen

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun MainScreen(navController: NavController) {
    val bottomNavItems = listOf(
        BottomNavItem("Home", "home", Icons.Default.Home),
        BottomNavItem("Chat", "chat", Icons.AutoMirrored.Default.Chat),
        BottomNavItem("Search", "search", Icons.Default.Search),
        BottomNavItem("Pets", "pets", Icons.Default.Pets),
        BottomNavItem("Profile", "profile", Icons.Default.Person)
    )

    var selectedItemIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = { selectedItemIndex = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name,
                                tint = if (selectedItemIndex == index) AccentGold else Color.Unspecified
                            )
                        },
                        label = {
                            Text(
                                text = item.name,
                                color = if (selectedItemIndex == index) AccentGold else Color.Unspecified
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItemIndex) {
                0 -> HomeScreen(navController)
                1 -> {
                    val sessionId = "current_session_id"
                    navController.navigate("${Routes.CHAT_BOT}?session=$sessionId")
                }
                2 -> SearchScreen(navController)
                3 -> PetListScreen(navController)
                4 -> ProfileScreen(navController)
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    FurrEverCareTheme {
        MainScreen(rememberNavController())
    }
}
