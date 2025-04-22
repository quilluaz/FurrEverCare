package com.jis_citu.furrevercare.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.R
import com.jis_citu.furrevercare.navigation.Routes
import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen

data class ChatSession(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: String,
    val iconRes: Int = R.drawable.logo_icon_colored
)

@Composable
fun ChatListScreen(navController: NavController) {
    val chatSessions = remember {
        mutableStateListOf(
            ChatSession("1", "Pet Nutrition Questions", "Try adding fiber-rich food.", "10:10 AM"),
            ChatSession("2", "Emergency Info", "Here's how to stop bleeding...", "Yesterday"),
            ChatSession("3", "General Inquiries", "Ask me anything about your pet!", "Last week")
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Background).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "AI Chat Sessions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    val newSessionId = System.currentTimeMillis().toString()
                    navController.navigate("${Routes.CHAT_BOT}?session=$newSessionId")
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("New Chat", color = Color.White)
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chatSessions) { session ->
                ChatSessionItem(session) {
                    navController.navigate("${Routes.CHAT_BOT}?session=${session.id}")
                }
            }
        }
    }
}

@Composable
fun ChatSessionItem(session: ChatSession, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = session.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1
            )
        }

        Text(
            text = session.timestamp,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = Color.LightGray
    )
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    FurrEverCareTheme {
        ChatListScreen(rememberNavController())
    }
}
