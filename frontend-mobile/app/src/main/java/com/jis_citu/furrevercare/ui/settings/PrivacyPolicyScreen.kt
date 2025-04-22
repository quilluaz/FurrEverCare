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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jis_citu.furrevercare.theme.FurrEverCareTheme

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    val scrollState = rememberScrollState()

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
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Privacy Policy for FurrEverCare",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Last Updated: April 20, 2025",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                PolicySection(
                    title = "1. Introduction",
                    content = "Welcome to FurrEverCare. We respect your privacy and are committed to protecting your personal data. This privacy policy will inform you about how we look after your personal data when you use our application and tell you about your privacy rights and how the law protects you."
                )

                PolicySection(
                    title = "2. Data We Collect",
                    content = "We may collect, use, store and transfer different kinds of personal data about you and your pets which we have grouped together as follows:\n\n" +
                            "• Personal Information: name, email address, phone number\n" +
                            "• Pet Information: pet name, species, breed, age, medical history\n" +
                            "• Technical Data: device information, IP address, login data\n" +
                            "• Usage Data: information about how you use our application"
                )

                PolicySection(
                    title = "3. How We Use Your Data",
                    content = "We use your data to provide and improve the FurrEverCare service. This includes:\n\n" +
                            "• Creating and managing your account\n" +
                            "• Providing pet care reminders and notifications\n" +
                            "• Connecting you with veterinarians\n" +
                            "• Improving our application and services\n" +
                            "• Sending you important updates about our service"
                )

                PolicySection(
                    title = "4. Data Sharing and Disclosure",
                    content = "We may share your personal information with:\n\n" +
                            "• Veterinarians you choose to connect with through our platform\n" +
                            "• Service providers who perform services on our behalf\n" +
                            "• Legal authorities when required by law\n\n" +
                            "We do not sell your personal information to third parties."
                )

                PolicySection(
                    title = "5. Data Security",
                    content = "We have implemented appropriate security measures to prevent your personal data from being accidentally lost, used, or accessed in an unauthorized way. We limit access to your personal data to those employees, agents, contractors, and other third parties who have a business need to know."
                )

                PolicySection(
                    title = "6. Your Rights",
                    content = "Under certain circumstances, you have rights under data protection laws in relation to your personal data, including the right to:\n\n" +
                            "• Request access to your personal data\n" +
                            "• Request correction of your personal data\n" +
                            "• Request erasure of your personal data\n" +
                            "• Object to processing of your personal data\n" +
                            "• Request restriction of processing your personal data\n" +
                            "• Request transfer of your personal data\n" +
                            "• Right to withdraw consent"
                )

                PolicySection(
                    title = "7. Contact Us",
                    content = "If you have any questions about this privacy policy or our privacy practices, please contact us at:\n\n" +
                            "Email: support@furrevercare.com"
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    FurrEverCareTheme {
        PrivacyPolicyScreen(rememberNavController())
    }
}
