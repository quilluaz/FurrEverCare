package com.jis_citu.furrevercare.ui.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items // Ensure this is imported
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api // Recommended for TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold // Import Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar // Import TopAppBar
import androidx.compose.material3.TopAppBarDefaults // Import TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color // Remove direct Color import if not needed for specific cases
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.theme.FurrEverCareTheme

// Assuming Notification data class is defined as before
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false,
    val iconRes: Int = R.drawable.logo_icon_colored // Default icon
)

@OptIn(ExperimentalMaterial3Api::class) // For TopAppBar
@Composable
fun NotificationsScreen(navController: NavController) {
    val notifications = remember {
        mutableStateListOf(
            Notification(
                id = "1",
                title = "FurrEverCare",
                message = "Welcome to FurrEverCare! Let's get you started on efficiently managing pet adoptions and health records. Keep track of animal well-being, streamline adoption processes, and ensure every pet finds the care they deserve—all in one place!",
                time = "5 min ago"
            ),
            Notification(
                id = "2",
                title = "Medication Reminder",
                message = "It's time for Max's heartworm medication. Don't forget to administer it today.",
                time = "1 hour ago"
            ),
            Notification(
                id = "3",
                title = "Appointment Reminder",
                message = "You have a vet appointment for Whiskers tomorrow at 2:00 PM.",
                time = "3 hours ago",
                isRead = true
            ),
            Notification(
                id = "4",
                title = "New Feature",
                message = "We've added a new feature to track your pet's exercise. Check it out in the latest update!",
                time = "Yesterday",
                isRead = true
            )
        )
    }

    FurrEverCareTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Notifications") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                                // Tint will be inherited from TopAppBarDefaults or can be set explicitly
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Use theme color
                        titleContentColor = MaterialTheme.colorScheme.onPrimary, // Use theme color
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary // Use theme color
                    )
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) // Use theme background
                    .padding(paddingValues) // Apply padding from Scaffold
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Add some overall padding for the list
            ) {
                if (notifications.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize() // Fill available space in LazyColumn
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No notifications yet.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(notifications, key = { it.id }) { notification ->
                        var expanded by remember { mutableStateOf(false) }

                        NotificationItem(
                            notification = notification,
                            expanded = expanded,
                            onExpandToggle = { expanded = !expanded },
                            onDismiss = {
                                notifications.remove(notification)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp)) // Increased spacing
                    }
                }
                // Optional: Add some bottom padding if needed, e.g., if there's a bottom bar
                // item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpandToggle),
        shape = RoundedCornerShape(16.dp), // Slightly larger radius
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surfaceContainerLowest // More subtle for read items
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh // Slightly elevated for unread
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = notification.iconRes), // Keep your icon logic
                    contentDescription = "Notification Icon",
                    modifier = Modifier
                        .size(36.dp) // Adjusted size
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface // Use theme color
                    )
                    Text(
                        text = notification.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Use theme color
                    )
                }

                if (expanded) { // Show dismiss only when expanded for a cleaner look
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp) // Slightly larger touch target
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant // Use theme color
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp)) // Adjusted spacing

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 2, // Show 2 lines when collapsed
                overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Use theme color
            )
        }
    }
}

// --- Previews ---
@Preview(showBackground = true, name = "Notifications Screen Light")
@Composable
fun NotificationsScreenPreviewLight() {
    FurrEverCareTheme(darkTheme = false) {
        NotificationsScreen(rememberNavController())
    }
}

@Preview(showBackground = true, name = "Notifications Screen Dark")
@Composable
fun NotificationsScreenPreviewDark() {
    FurrEverCareTheme(darkTheme = true) {
        NotificationsScreen(rememberNavController())
    }
}


@Preview(showBackground = true, name = "Notification Item Unread")
@Composable
fun NotificationItemUnreadPreview() {
    FurrEverCareTheme {
        NotificationItem(
            notification = Notification(
                id = "1",
                title = "FurrEverCare System Update",
                message = "Welcome to FurrEverCare! We've updated our privacy policy. Please review the changes.",
                time = "5 min ago",
                isRead = false
            ),
            expanded = false,
            onExpandToggle = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "Notification Item Read Expanded")
@Composable
fun NotificationItemReadExpandedPreview() {
    FurrEverCareTheme {
        NotificationItem(
            notification = Notification(
                id = "2",
                title = "Medication Reminder",
                message = "It's time for Max's heartworm medication. Don't forget to administer it today. This is a longer message to test how text overflows and expands correctly within the card.",
                time = "1 hour ago",
                isRead = true
            ),
            expanded = true,
            onExpandToggle = {},
            onDismiss = {}
        )
    }
}