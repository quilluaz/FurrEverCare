package com.jis_citu.furrevercare.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.theme.FurrEverCareTheme

data class FAQ(
    val question: String,
    val answer: String
)

@Composable
fun FAQsScreen(navController: NavController) {
    val faqs = listOf(
        FAQ(
            "What is FurrEverCare?",
            "FurrEverCare is a comprehensive pet care app designed to help pet owners manage their pets' health, track medications, connect with veterinarians, and access a community of fellow pet owners."
        ),
        FAQ(
            "How do I add a new pet to my profile?",
            "To add a new pet, go to the 'My Pets' section from your profile, then tap on the '+' button. Fill in your pet's details including name, species, breed, age, and upload a photo if you'd like."
        ),
        FAQ(
            "Can I set medication reminders for my pets?",
            "Yes! You can set medication reminders by going to your pet's profile, selecting 'Medications', and then adding a new medication with the dosage and schedule. The app will send you notifications based on your settings."
        ),
        FAQ(
            "How does the vet chat feature work?",
            "The vet chat feature allows you to connect with licensed veterinarians for non-emergency advice. You can start a chat from the 'Chat' tab, select 'New Consultation', and choose a vet based on their specialty and availability."
        ),
        FAQ(
            "Is my pet's information secure?",
            "Yes, we take data security seriously. All your pet's information is encrypted and stored securely. We do not share your personal or pet information with third parties without your consent."
        ),
        FAQ(
            "How can I delete my account?",
            "To delete your account, go to 'Profile' > 'Settings' > 'Account' > 'Delete Account'. Please note that this action is permanent and will remove all your data from our servers."
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(faqs) { faq ->
                    FAQItem(faq)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun FAQItem(faq: FAQ) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FAQsScreenPreview() {
    FurrEverCareTheme {
        FAQsScreen(rememberNavController())
    }
}
