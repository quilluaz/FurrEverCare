package com.jis_citu.furrevercare.ui.chat

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.theme.Background
import com.jis_citu.furrevercare.theme.FurrEverCareTheme
import com.jis_citu.furrevercare.theme.PrimaryGreen

@Composable
fun ChatDetailScreen(navController: NavController, chatId: String) {
    val chatPartner = remember {
        when (chatId) {
            "1" -> "Dr. Sarah Johnson"
            "2" -> "Pet Care Bot"
            "3" -> "Dr. Michael Lee"
            "4" -> "Pet Grooming"
            else -> "Unknown"
        }
    }

    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "Hello! How can I help you today?", false, "10:00 AM"),
            ChatMessage("2", "Hi, I have a question about my dog's medication.", true, "10:02 AM"),
            ChatMessage("3", "Of course, I'd be happy to help. What specific medication are you asking about?", false, "10:03 AM"),
            ChatMessage("4", "It's the heartworm prevention medication you prescribed last month.", true, "10:05 AM")
        )
    }

    var messageText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Chat header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = chatPartner,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(messages) { message ->
                    ChatMessageItem(message)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Message input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* Handle attachment */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach"
                    )
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            val userMessage = ChatMessage(
                                id = (messages.size + 1).toString(),
                                message = messageText,
                                isFromUser = true,
                                timestamp = "Just now"
                            )
                            messages.add(userMessage)

                            // Simulate response
                            val botResponse = ChatMessage(
                                id = (messages.size + 1).toString(),
                                message = "I'll look into that for you. Let me check your records.",
                                isFromUser = false,
                                timestamp = "Just now"
                            )
                            messages.add(botResponse)

                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatDetailScreenPreview() {
    FurrEverCareTheme {
        ChatDetailScreen(rememberNavController(), "1")
    }
}
