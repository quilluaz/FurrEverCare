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
fun DataUsageScreen(navController: NavController) {
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
                    text = "Data Usage",
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
                    text = "Data Usage Information",
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

                DataUsageSection(
                    title = "1. Network Data Usage",
                    content = "FurrEverCare uses your device's internet connection to sync pet data, download updates, and enable communication with veterinarians. The amount of data used depends on your activity within the app:\n\n" +
                            "• Text-based chat: Minimal data usage (approximately 1-5 KB per message)\n" +
                            "• Image uploads/downloads: 0.5-3 MB per image depending on resolution\n" +
                            "• Video consultations: 5-15 MB per minute depending on quality settings\n\n" +
                            "To minimize data usage, we recommend connecting to Wi-Fi when uploading multiple images or using video consultation features."
                )

                DataUsageSection(
                    title = "2. Storage Usage",
                    content = "FurrEverCare stores data locally on your device to ensure quick access and offline functionality. The app typically uses:\n\n" +
                            "• Base application: 50-80 MB\n" +
                            "• Pet profiles: 1-10 MB per pet (depending on number of photos)\n" +
                            "• Cached data: Up to 200 MB (automatically managed)\n\n" +
                            "You can clear cached data through your device's application settings without losing important pet information."
                )

                DataUsageSection(
                    title = "3. Background Data",
                    content = "FurrEverCare uses minimal background data to:\n\n" +
                            "• Sync pet information across devices\n" +
                            "• Deliver medication and appointment reminders\n" +
                            "• Check for important app updates\n\n" +
                            "Background data usage is typically less than 5 MB per week under normal usage patterns."
                )

                DataUsageSection(
                    title = "4. Data Optimization",
                    content = "We continuously work to optimize data usage in our app. Images are compressed before upload, and we use efficient data transfer protocols to minimize bandwidth consumption. You can further optimize data usage by adjusting settings in the app:\n\n" +
                            "• Enable 'Data Saver Mode' in Settings\n" +
                            "• Adjust notification frequency\n" +
                            "• Set media quality preferences for uploads and downloads"
                )

                DataUsageSection(
                    title = "5. Updates and Changes",
                    content = "We regularly update our app to improve performance and add new features. Updates typically range from 20-50 MB and are recommended to be downloaded over Wi-Fi. You can control when updates are installed through your device's app store settings."
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DataUsageSection(title: String, content: String) {
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
fun DataUsageScreenPreview() {
    FurrEverCareTheme {
        DataUsageScreen(rememberNavController())
    }
}
