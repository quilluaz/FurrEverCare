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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false,
    val iconRes: Int = R.drawable.logo_icon_colored
)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF9AB86C)) // Using the green background from the image
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

                // Empty space to balance the back button
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Notifications list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(notifications) { notification ->
                    var expanded by remember { mutableStateOf(false) }

                    NotificationItem(
                        notification = notification,
                        expanded = expanded,
                        onExpandToggle = { expanded = !expanded },
                        onDismiss = {
                            notifications.remove(notification)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for navigation bar
                }
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
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White.copy(alpha = 0.9f) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App logo
                Image(
                    painter = painterResource(id = notification.iconRes),
                    contentDescription = "Notification Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Title and time
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = notification.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Dismiss button
                if (expanded) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Message
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    FurrEverCareTheme {
        NotificationsScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemPreview() {
    FurrEverCareTheme {
        NotificationItem(
            notification = Notification(
                id = "1",
                title = "FurrEverCare",
                message = "Welcome to FurrEverCare! Let's get you started on efficiently managing pet adoptions and health records.",
                time = "5 min ago"
            ),
            expanded = true,
            onExpandToggle = {},
            onDismiss = {}
        )
    }
}
