package com.jis_citu.furrevercare.ui.profile

import androidx.compose.foundation.BorderStroke // For OutlinedButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.* // Keep for other icons (AccountCircle, Edit, Email, HelpOutline, Notifications, Settings)
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.model.User // Your User model
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
// Import ViewModel and related classes
import com.jis_citu.furrevercare.ui.profile.viewmodel.ProfileNavigationEvent
import com.jis_citu.furrevercare.ui.profile.viewmodel.ProfileUiState
import com.jis_citu.furrevercare.ui.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val route: String
)

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is ProfileNavigationEvent.Navigate -> {
                    navController.navigate(event.route) {
                        event.popUpToRoute?.let { popUpTo(it) { inclusive = event.inclusive } }
                        launchSingleTop = event.launchSingleTop
                    }
                }
            }
        }
    }

    ProfileScreenContent(
        uiState = uiState,
        navController = navController,
        onLogoutClick = viewModel::logoutUser,
        onEditProfileClick = { navController.navigate(Routes.EDIT_PROFILE) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    navController: NavController, // For menu item navigation
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val user = uiState.user

    val menuItems = remember {
        listOf(
            ProfileMenuItem(Icons.Filled.Notifications, "Notifications", Routes.NOTIFICATIONS),
            ProfileMenuItem(Icons.Filled.Settings, "Settings", Routes.SETTINGS),
            ProfileMenuItem(Icons.AutoMirrored.Filled.HelpOutline, "FAQs", Routes.FAQS),
            ProfileMenuItem(Icons.Filled.Email, "Contact Us", Routes.CONTACT_US)
        )
    }

    FurrEverCareTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Profile") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (!uiState.isUserLoggedIn || user == null) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = "Not logged in",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            uiState.errorMessage ?: "Please log in to view your profile.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = if (uiState.errorMessage != null && uiState.isUserLoggedIn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (!uiState.isUserLoggedIn) {
                            Button(onClick = {
                                navController.navigate(Routes.WELCOME_AUTH) {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }) {
                                Text("Go to Login")
                            }
                        }
                    }
                } else { // User data available
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(top = 24.dp, bottom = 16.dp) // Adjusted top padding
                    ) {
                        item {
                            ProfileHeaderNoImage(
                                userName = user.name,
                                userEmail = user.email,
                                onEditProfileClick = onEditProfileClick
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    menuItems.forEachIndexed { index, item ->
                                        ProfileMenuItemRow(
                                            item = item,
                                            onClick = { navController.navigate(item.route) }
                                        )
                                        if (index < menuItems.size - 1) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp), // Less vertical padding
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f) // Softer divider
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp)) // More space after menu card
                        }

                        item {
                            Button(
                                onClick = onLogoutClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f) // Slightly narrower button
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp) // Consistent rounding
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout Icon", modifier = Modifier.size(ButtonDefaults.IconSize))
                                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Logout", fontWeight = FontWeight.Medium) // Bolder text
                            }
                            Spacer(modifier = Modifier.height(32.dp)) // More space after logout
                        }

                        item {
                            Text(
                                text = "FurrEverCare v1.0.0", // Replace with your actual app version
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderNoImage(
    userName: String,
    userEmail: String,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp), // Add some bottom padding to the header section
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp) // Standard size
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer) // Use primary container for a colored bg
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person, // Changed to Person icon
                contentDescription = "Profile Picture Placeholder",
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer // Good contrast on primaryContainer
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = userName.ifBlank { "FurrEver User" }, // Fallback if name is blank
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userEmail.ifBlank { "No email provided" }, // Fallback if email is blank
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp)) // Increased space before button

        OutlinedButton(
            onClick = onEditProfileClick,
            modifier = Modifier.height(40.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfileMenuItemRow(item: ProfileMenuItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp), // Increased vertical padding for touch
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = MaterialTheme.colorScheme.primary, // Consistent icon tint
            modifier = Modifier.size(24.dp) // Standard icon size
        )
        Spacer(modifier = Modifier.width(20.dp)) // Increased spacing
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium, // Using titleMedium for menu items
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) // Slightly more visible
        )
    }
}
